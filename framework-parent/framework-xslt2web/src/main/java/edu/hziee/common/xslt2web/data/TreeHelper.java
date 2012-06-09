package edu.hziee.common.xslt2web.data;

import java.util.EnumSet;

import javax.servlet.http.HttpServletRequest;

import edu.hziee.common.xslt2web.configxml.ModuleXml;
import edu.hziee.common.xslt2web.exception.ToolkitException;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sys.QueryParamCondition;
import edu.hziee.common.xslt2web.sys.SQLParamBuilder;
import edu.hziee.common.xslt2web.sys.TreeFieldGroup;
import edu.hziee.common.xslt2web.sysutil.DataSetUtil;

public final class TreeHelper {
	public final static String SUB_TREE_OPER = "SubTree";
	public final static String MOVE_OPER = "Move";
	public final static String DEFAULT_OPER = "Tree";
	public final static String SORT_OPER = "Sort";

	private WebUpdateDataSet host;
	private TreeXmlTableResolver treeResolver;
	private ModuleXml module;

	public TreeHelper(WebUpdateDataSet host, ModuleXml module) {
		super();
		this.host = host;
		this.module = module;

		if (host instanceof IMainTableResolver) {
			AbstractXmlTableResolver mainResolver = ((IMainTableResolver) host)
					.getMainResolver();
			if (mainResolver instanceof TreeXmlTableResolver)
				this.treeResolver = (TreeXmlTableResolver) mainResolver;
		}

		// Debug.Assert(mainResolver != null,
		// string.Format("类型%s不支持IMainTableResolver接口，请更换支持IMainTableResolver的DataSet",
		// host.GetType().ToString()));
		// Debug.Assert(fTreeResolver != null,
		// string.Format("表%s必须从Tree2XmlTableResolver继承，否则无法完成树形的操作",
		// mainResolver.MainResolver.TableName));
		host.addFilledCustomListener(new FilledCustomListener() {
			public void filledCustomTables(FilledCustomEventArgs e) {
				host_FilledCustomTables(e);
			}
		});
		host.addFillingUpdateListener(new FillingUpdateListener() {
			public void fillingUpdateTables(FillingUpdateEventArgs e) {
				host_FillingUpdateTables(e);
			}
		});
		host.addFilledUpdateListener(new FilledUpdateListener() {
			public void filledUpdateTables(FilledUpdateEventArgs e) {
				host_FilledUpdateTables(e);
			}
		});
	}

	private void changeChildLayer(String id, String layer, String isLeaf) {
		if ("0".equals(isLeaf)) {
			String layerField = treeResolver.getTreeFields().getLayer();
			String parentField = treeResolver.getTreeFields().getParentID();
			treeResolver.selectWithParam(parentField, id);
			DataRow[] childRow = null;// treeResolver.getHostTable().select(String.format("%s
			// = %s", parentField, id), layerField
			// + " ASC");
			for (int i = 0; i < childRow.length; i++) {
				String childLayer = childRow[i].getItem(layerField).toString();
				String lastThree = childLayer.substring(
						childLayer.length() - 3, childLayer.length());
				childRow[i].setItem(layerField, String.format("%s%s", layer,
						lastThree));
				changeChildLayer(childRow[i].getItem(
						treeResolver.getTreeFields().getId()).toString(),
						childRow[i].getItem(layerField).toString(), childRow[i]
								.getItem(
										treeResolver.getTreeFields()
												.getIsLeaf()).toString());
			}
		}
	}

	private int getRowNum(DataTable table, String id) {
		for (int i = 0; i < table.getRows().size(); i++) {
			if (id.equals(table.getRows().getItem(i).getItem(
					treeResolver.getTreeFields().getId()).toString()))
				return i;
		}
		return -1;
	}

	private void swapLayer(int rowNum1, int rowNum2) {
		String layerField = treeResolver.getTreeFields().getLayer();
		String leafField = treeResolver.getTreeFields().getIsLeaf();

		DataRow row1 = treeResolver.getHostTable().getRows().getItem(rowNum1);
		DataRow row2 = treeResolver.getHostTable().getRows().getItem(rowNum2);
		String tempLayer = row1.getItem(layerField).toString();
		row1.setItem(layerField, row2.getItem(layerField));
		row2.setItem(layerField, tempLayer);
		changeChildLayer(row1.getItem(treeResolver.getTreeFields().getId())
				.toString(), row1.getItem(layerField).toString(), row1.getItem(
				leafField).toString());
		changeChildLayer(row2.getItem(treeResolver.getTreeFields().getId())
				.toString(), row2.getItem(layerField).toString(), row2.getItem(
				leafField).toString());
	}

	private void moveTree(HttpServletRequest request) {
		TreeUtil.moveTree(treeResolver, treeResolver.getTreeFields(), request
				.getParameter("Oid"), request.getParameter("Nid"));
		host.commit();
	}

	private void sortTree(HttpServletRequest request) {
		String id = request.getParameter("ID");// 要移动节点ID
		String direct = request.getParameter("Direct");// 移动方向

		TreeFieldGroup fields = treeResolver.getTreeFields();
		IParamBuilder idBuilder = TreeUtil.getIdParamBuilder(fields, fields
				.getId(), id);
		String layer = DataSetUtil.executeScalar(
				host.getConnection(),
				String.format("SELECT %s FROM %s WHERE %s", treeResolver
						.getTreeFields().getLayer(), treeResolver
						.getTableName(), idBuilder.getSQL()),
				idBuilder.getParams()).toString();
		String parentLayer = layer.substring(0, layer.length() - 3);

		treeResolver.setCommands(EnumSet.of(AdapterCommand.Update));
		IParamBuilder layerBuilder = SQLParamBuilder.getSingleSQL(
				TypeCode.String, treeResolver.getTreeFields().getLayer(),
				"LIKE", parentLayer + "___");
		treeResolver.select(String.format("WHERE %s ORDER BY %s", layerBuilder
				.getSQL(), treeResolver.getTreeFields().getLayer()),
				layerBuilder.getParams());
		if (treeResolver.getHostTable() == null
				|| treeResolver.getHostTable().getRows().size() == 0)
			return;

		int rowNum = getRowNum(treeResolver.getHostTable(), id);
		if (rowNum == -1)
			return;
		// 根据移动方向，执行不同操作
		direct = direct.toLowerCase();
		if ("up".equals(direct)) {
			if (rowNum == 0)// 已经最前，不能向上移动
				return;
			swapLayer(rowNum, rowNum - 1);
		} else if ("down".equals(direct)) {
			if (rowNum == treeResolver.getHostTable().getRows().size() - 1)// 已经最后，不能向下移动
				return;
			swapLayer(rowNum, rowNum + 1);
		}
		host.commit();
	}

	private void addIsParentInfo() {
		DataTable dt = DataSetUtil.createDataTable("_TREE_PARENT_INFO",
				"ROOT_ID", "IS_PARENT");
		int isParentId;
		String rootId;
		if (treeResolver.isParentID()) {
			rootId = treeResolver.getRootID();
			isParentId = 1;
		} else {
			rootId = getRootID();
			isParentId = 0;
		}
		dt.getRows().add(new Object[] { rootId, isParentId });
		host.getTables().add(dt);
	}

	private String getRootID() {
		String rootId;
		TreeFieldGroup group = treeResolver.getTreeFields();
		IParamBuilder builder = TreeUtil.getIdParamBuilder(group,
				group.getId(), treeResolver.getRootID());
		rootId = DataSetUtil.executeScalar(
				host.getConnection(),
				String.format("SELECT %s FROM %s WHERE %s",
						group.getParentID(), treeResolver.getTableName(),
						builder.getSQL()), builder.getParams()).toString();
		return rootId;
	}

	private void fillChildTree(HttpServletRequest request) {
		String parentID = request.getParameter("ID");
		QueryParamCondition sqlCon = new QueryParamCondition();
		DataTable dt = DataSetUtil.createDataTable("_TREE_PARENT_INFO",
				"ROOT_ID");
		host.getTables().add(dt);
		TreeFieldGroup fields = treeResolver.getTreeFields();
		if (parentID == null) {
			String exID = request.getParameter("ExID");
			IParamBuilder builder;
			if (exID == null) {
				if (treeResolver.isParentID())
					builder = TreeUtil.getIdParamBuilder(fields, fields
							.getParentID(), treeResolver.getRootID());
				else
					builder = TreeUtil.getIdParamBuilder(fields,
							fields.getId(), treeResolver.getRootID());
			} else
				builder = new LayerParamBuilder(treeResolver, treeResolver.getConnection(), exID);
			if (treeResolver.isParentID())
				parentID = treeResolver.getRootID();
			else
				parentID = getRootID();
			sqlCon.addSQL(builder);
		} else {
			sqlCon.addSQL(TreeUtil.getIdParamBuilder(fields, fields
					.getParentID(), parentID));
		}
		dt.getRows().add(new Object[] { parentID });
		if (host.getFilterSQL() != null)
			sqlCon.addSQL(host.getFilterSQL().toString());
		String order = "";
		if (host.getDefaultSortField() >= 0
				&& host.getDefaultSortField() < treeResolver.getDataXml()
						.getTable().getFields().length) {
			// String orderField =
			// treeResolver.getDataXml().getTable().getField()[fHost.DefaultSortField].FieldName;
			// order = " ORDER BY " + orderField;
			if (!"".equals(host.getDefaultOrder()))
				order += " " + host.getDefaultOrder();
		} else
			// if (DefaultSortField == 0)
			order = " ORDER BY " + fields.getLayer();

		String sql = String
				.format(
						"SELECT %s AS ID, %s AS NAME, %s AS ISLEAF, %s AS PARENT_ID FROM %s WHERE %s%s",
						fields.getId(), fields.getName(), fields.getIsLeaf(),
						fields.getParentID(), treeResolver.getTableName(),
						sqlCon.getSql(), order);
		treeResolver.selectSql("_TREE", sql, sqlCon.getParams());
	}

	private void host_FilledCustomTables(FilledCustomEventArgs e) {
		String operation = e.getOperation();
		if (DEFAULT_OPER.equals(operation))
			addIsParentInfo();
		else if (SUB_TREE_OPER.equals(operation))
			fillChildTree(e.getRequest());
		else if (MOVE_OPER.equals(operation))
			moveTree(e.getRequest());
		else if (SORT_OPER.equals(operation))
			sortTree(e.getRequest());
	}

	private void host_FillingUpdateTables(FillingUpdateEventArgs e) {
		if (e.getStyle() == PageStyle.Delete)
			e.getHandled().setItem(treeResolver, true);
	}

	private void host_FilledUpdateTables(FilledUpdateEventArgs e) {
		if (e.getStyle() == PageStyle.Detail) {
			Object rootID;
			if (treeResolver.isParentID())
				rootID = treeResolver.getRootID();
			else {
				TreeFieldGroup fields = treeResolver.getTreeFields();
				IParamBuilder idBuilder = TreeUtil.getIdParamBuilder(fields,
						fields.getId(), treeResolver.getRootID());
				String sql = String.format("SELECT %s from %s where %s", fields
						.getParentID(), treeResolver.getTableName(), idBuilder
						.getSQL());
				rootID = DataSetUtil.executeScalar(host.getConnection(), sql,
						idBuilder.getParams());
			}
			DataTable dt = DataSetUtil.createDataTable("_TREE_ROOT_INFO",
					"ROOT_ID");
			dt.getRows().add(new Object[] { rootID });
			host.getTables().add(dt);
		}
	}

	public void delete() {
		TreeUtil.deleteTree(treeResolver, treeResolver.getTreeFields(), host
				.getRequest().getParameter("ID"));
	}

	public void committingData() {
		int count = treeResolver.getHostTable().getRows().size();
		for (int i = 0; i < count; i++) {
			DataRow row = treeResolver.getHostTable().getRows().getItem(i);
			if (row.getRowState() == DataRowState.Added)
				TreeUtil.setAddedRow(treeResolver, row, treeResolver
						.getTreeFields());
		}
	}

	public String getDefaultPage(boolean isPost, PageStyle style,
			String operation, String retURL) {
		String moduleUrl = module.getDefaultPage(treeResolver, isPost, style);
		if (!"".equals(moduleUrl))
			return moduleUrl;

		if (isPost) {
			String saveMethod = host.getSaveMethod().toLowerCase();
			if ("save".equals(saveMethod)) {
				if (treeResolver.getKeyCount() > 1)
					throw new ToolkitException(String.format("树型结构%s不能有多个主键!",
							treeResolver.getTableName()));
				Object key = treeResolver.getHostTable().getRows().getItem(
						treeResolver.getHostTable().getRows().size() - 1)
						.getItem(treeResolver.getTreeFields().getId());
				return String.format(
						"../toolkit/webtreexmlpage?Source=%s&ExID=%s", module
								.getXmlFile(), key);

			} else if ("savenew".equals(saveMethod)) {
				String nowURL = "";
				if (style == PageStyle.Insert) // 新建
					nowURL = host.getRequest().getRequestURI().toString();
				else if (style == PageStyle.Update)// 修改
				{
					Object parentID = treeResolver.getHostTable().getRows()
							.getItem(0).getItem(
									treeResolver.getTreeFields().getParentID());
					return String.format(
							"../toolkit/webinsertxmlpage?Source=%s&PID=%s",
							module.getXmlFile(), parentID);
				}
				return nowURL;
			}
		} else
			switch (style) {
			case Update:
				return String.format(
						"../toolkit/webtreexmlpage?Source=%s&ExID=%s", module
								.getXmlFile(), host.getRequest().getParameter(
								"ID"));
			}

		return String.format("../toolkit/webtreexmlpage?Source=%s", module
				.getXmlFile());
	}

	public String getXsltFile(boolean isIe, PageStyle style, String operation) {
		return module.getXsltFile(style, operation);
	}

	public String getDataXslt(PageStyle style, String operation) {
		if (style == PageStyle.Custom && DEFAULT_OPER.equals(operation))
			return "Tk2Template\\TreeTrans.xslt";
		else
			return "";
	}

	public boolean getSupportDoubleTransform(PageStyle style, String operation) {
		if (style == PageStyle.Custom
				&& (SUB_TREE_OPER.equals(operation) || MOVE_OPER
						.equals(operation)))
			return false;
		return true;
	}

	public String getDefaultXsltTemplate(PageStyle style, String operation) {
		String xslt = module.getDefaultXsltTemplate(style, operation);
		if ("".equals(xslt)) {
			if (style == PageStyle.Custom && DEFAULT_OPER.equals(operation))
				return "..\\Tk2Template\\TreeTemplate2.xslt";
			else if (style == PageStyle.Detail)
				return "..\\Tk2Template\\TreeDetailTemplate.xslt";
			else
				return "";
		} else
			return xslt;
	}

}
