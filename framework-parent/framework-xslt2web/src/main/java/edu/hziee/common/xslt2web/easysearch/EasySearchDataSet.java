package edu.hziee.common.xslt2web.easysearch;

import javax.servlet.http.HttpServletRequest;

import edu.hziee.common.xslt2web.data.DataRow;
import edu.hziee.common.xslt2web.data.DataSet;
import edu.hziee.common.xslt2web.data.DataTable;
import edu.hziee.common.xslt2web.data.TreeUtil;
import edu.hziee.common.xslt2web.data.WebBaseDataSet;
import edu.hziee.common.xslt2web.sys.ExpressionDataListener;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sys.IXmlHttpPostPage;
import edu.hziee.common.xslt2web.sys.IXmlPage;
import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sys.TreeFieldGroup;
import edu.hziee.common.xslt2web.sys.WebDataListener;
import edu.hziee.common.xslt2web.sysutil.DataSetUtil;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class EasySearchDataSet extends WebBaseDataSet implements
		IXmlHttpPostPage, IXmlPage {
	public static final String FAST_SEARCH = "FastSearch";

	public static final String DIALOG = "Dialog";

	public static final String TREE = "TreeExpand";

	private EasySearch item;

	private String regName;

	private EasySearchType codeType;

	private ExpressionDataListener dataListener;

	private void searchData(String value, DataSet postDataSet,
			DataTable refTable) {
		if (item == null)
			item = EasySearchUtil.getRegEasySearch(this, regName);
		codeType = item.getType();
		item.setDataSet(this, postDataSet);

		item.fastSearch(value, refTable);
	}

	public String getAlertString(PageStyle style, String operation,
			HttpServletRequest request, DataSet postDataSet) {
		return "";
	}

	public String getJScript(PageStyle style, String operation) {
		return null;
	}

	public String post(PageStyle style, String operation,
			HttpServletRequest request, DataSet postDataSet) {
		DataTable selectTable;
		DataRow row;
		String value;
		if (FAST_SEARCH.equals(operation) || DIALOG.equals(operation)) {
			selectTable = postDataSet.getTables().getItem("SELECT");
			// Debug.Assert(selectTable != null, "没有提交SELECT数据！请检查提交的js函数。");
			row = selectTable.getRows().getItem(0);
			value = row.getItem("VALUE").toString();
			regName = row.getItem("TYPE").toString();
			searchData(value, postDataSet, postDataSet.getTables().getItem(
					"REF"));
			return null;
		}
		return null;
	}

	public String getDefaultPage(boolean isPost, PageStyle style,
			String operation, String retURL) {
		return retURL;
	}

	public String getXsltFile(boolean isIe, PageStyle style, String operation) {
		if (FAST_SEARCH.equals(operation))
			return "..\\Bin\\SelectAC.xslt";
		else if (DIALOG.equals(operation)) {
			switch (codeType) {
			case Normal:
				return "..\\Bin\\EasySearchDialog.xslt";
			case Level:
			case Level0:
			case Tree:
				return "..\\Bin\\EasySearchTree.xslt";
			}
		}
		return null;
	}

	private void addLevelParentInfo() {
		DataTable dt = DataSetUtil.createDataTable("_TREE_PARENT_INFO",
				"ROOT_ID", "IS_PARENT", "IS_LEAF_SELECT");
		dt.getRows().add(new Object[] { "0", true, false });
		this.getTables().add(dt);
	}

	private void addTreeParentInfo() {
		DataTable dt = DataSetUtil.createDataTable("_TREE_PARENT_INFO",
				"ROOT_ID", "IS_PARENT", "IS_LEAF_SELECT");
		dataListener = new WebDataListener(this, this);
		int isParentId;
		String rootId;
		if (item.getTree().isParentID()) {
			rootId = item.getTree().getBaseValue().toString(
					getAppGlobal().getRegsCollection(), dataListener);
			isParentId = 1;
		} else {
			rootId = getRootID();
			isParentId = 0;
		}

		dt.getRows().add(
				new Object[] { rootId, isParentId,
						item.getTree().isOnlyLeafSelect() });
		this.getTables().add(dt);
	}

	private String getRootID() {
		String rootId;
		TreeFieldGroup group = item.getTree().getTreeFields();
		IParamBuilder builder = TreeUtil.getIdParamBuilder(group,
				group.getId(), item.getTree().getBaseValue().toString(
						this.getAppGlobal().getRegsCollection(), dataListener));
		rootId = DataSetUtil.executeScalar(
				getConnection(),
				String.format("SELECT %s FROM %s WHERE %s",
						group.getParentID(), item.getTableName(), builder
								.getSQL()), builder.getParams()).toString();
		return rootId;
	}

	public void setData(boolean isPost, PageStyle style, String operation,
			HttpServletRequest request) {
		// Debug.Assert(operation == DIALOG, "在Get情况下，只能处理Dialog模式");
		regName = request.getParameter("Type");
		if (item == null)
			item = EasySearchUtil.getRegEasySearch(this, regName);
		codeType = item.getType();

		item.setDataSet(this, null);
		switch (codeType) {
		case Normal:
			String refValue = request.getParameter("RefValue");
			DataTable refTable = null;
			if (!StringUtil.isEmpty(refValue)) {
				String xml = StringUtil.getXmlString(refValue);
				DataSet dataSet = DataSetUtil.xmlToDataSet(xml);
				refTable = dataSet.getTables().getItem("REF");
			}
			searchData("", null, refTable);
			break;
		case Level:
		case Level0:
			addLevelParentInfo();
			break;
		case Tree:
			addTreeParentInfo();
			break;
		}
	}
}
