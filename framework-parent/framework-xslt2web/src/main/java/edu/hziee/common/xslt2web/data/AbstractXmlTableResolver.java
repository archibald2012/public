package edu.hziee.common.xslt2web.data;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.configxml.DataXml;
import edu.hziee.common.xslt2web.configxml.FieldConfigItem;
import edu.hziee.common.xslt2web.configxml.HtmlCtrlType;
import edu.hziee.common.xslt2web.configxml.ResolverConfigItem;
import edu.hziee.common.xslt2web.constraint.BaseConstraint;
import edu.hziee.common.xslt2web.constraint.ConstraintCollection;
import edu.hziee.common.xslt2web.constraint.DateConstraint;
import edu.hziee.common.xslt2web.constraint.DateRangeConstraint;
import edu.hziee.common.xslt2web.constraint.DoubleConstraint;
import edu.hziee.common.xslt2web.constraint.ErrorObjectCollection;
import edu.hziee.common.xslt2web.constraint.IntConstraint;
import edu.hziee.common.xslt2web.constraint.NotEmptyConstraint;
import edu.hziee.common.xslt2web.constraint.StringLengthConstraint;
import edu.hziee.common.xslt2web.easysearch.BaseEasySearchField;
import edu.hziee.common.xslt2web.easysearch.CodeTableCollection;
import edu.hziee.common.xslt2web.easysearch.EasySearchField;
import edu.hziee.common.xslt2web.easysearch.EasySearchFieldCollection;
import edu.hziee.common.xslt2web.exception.ToolkitException;
import edu.hziee.common.xslt2web.provider.DateSpanListSearch;
import edu.hziee.common.xslt2web.provider.DateTimeSpanListSearch;
import edu.hziee.common.xslt2web.provider.SimpleDateListSearch;
import edu.hziee.common.xslt2web.provider.SpanListSearch;
import edu.hziee.common.xslt2web.right.DataRightEventArgs;
import edu.hziee.common.xslt2web.right.IDataRight;
import edu.hziee.common.xslt2web.sys.AppSetting;
import edu.hziee.common.xslt2web.sys.ApplicationGlobal;
import edu.hziee.common.xslt2web.sys.BaseListSearch;
import edu.hziee.common.xslt2web.sys.DefaultListSearch;
import edu.hziee.common.xslt2web.sys.EventArgs;
import edu.hziee.common.xslt2web.sys.ExpressionDataListener;
import edu.hziee.common.xslt2web.sys.INeedWebData;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sys.IWebData;
import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sys.QueryParamCondition;
import edu.hziee.common.xslt2web.sys.SessionGlobal;
import edu.hziee.common.xslt2web.sys.WebDataListener;
import edu.hziee.common.xslt2web.sysutil.DateUtil;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class AbstractXmlTableResolver extends AbstractXmlDataAdapter implements
		INeedWebData, IWebData {
	private final EnumSet<UpdateKindType> INSERT_SET = EnumSet.of(
			UpdateKindType.Insert, UpdateKindType.InsUpd);
	private final EnumSet<UpdateKindType> UPDATE_SET = EnumSet.of(
			UpdateKindType.Update, UpdateKindType.InsUpd);

	private DataXml dataXml;
	private String rightType;
	private String ownerField;
	private HashMap<String, BaseListSearch> listSearches;
	private ErrorObjectCollection errorObjects;
	private ConstraintCollection constraints;
	private CodeTableCollection regCodeTables;
	private boolean autoUpdating;
	private DataXml tempDataXml;
	private ArrayList<String> configCodeTables;
	private EasySearchFieldCollection easySearchFields;
	private ArrayList<FieldConfigItem> defaultValue;
	private ArrayList<FieldConfigItem> updating;
	private boolean isAutoInc;
	private boolean hasAddEasySearches;
	private INonUIResolvers nonUIResolvers;

	private HttpServletRequest request;
	private HttpServletResponse response;
	private ApplicationGlobal appGbl;
	private SessionGlobal sessionGbl;
	private ExpressionDataListener exprListener;

	private DataNavEventArgs naviArgs;
	private NavigateDataListener naviDataListener;

	protected AbstractXmlTableResolver(BaseDataSet hostDataSet) {
		super(hostDataSet);

		autoUpdating = true;
		configCodeTables = new ArrayList<String>();
		easySearchFields = new EasySearchFieldCollection();
		hasAddEasySearches = false;
	}

	protected void addCodeTables(CodeTableCollection codeTables, PageStyle style) {
		for (String codeTable : configCodeTables)
			codeTables.add(appGbl.getRegsCollection(), codeTable);
	}

	private void addCodeTables(FieldConfigItem item, PageStyle style) {
		if (!StringUtil.isEmpty(item.getCodeTable()))
			configCodeTables.add(item.getCodeTable());
	}

	private void addConstraints(FieldConfigItem item) {
		switch (item.getFieldDataType()) {
		case Int:
			if (FieldConfigItem.TEXT.contains(item.getHtmlCtrl().getHtmlCtrl()))
				getConstraints().add(
						new IntConstraint(item.getFieldName(), item
								.getDisplayName()));
			break;
		case Double:
		case Money:
			if (FieldConfigItem.TEXT.contains(item.getHtmlCtrl().getHtmlCtrl()))
				getConstraints().add(
						new DoubleConstraint(item.getFieldName(), item
								.getDisplayName()));
			break;
		case Date:
		case DateTime:
			if (item.getHtmlCtrl().getHtmlCtrl() == HtmlCtrlType.Text) {
				getConstraints().add(
						new DateConstraint(item.getFieldName(), item
								.getDisplayName()));
				getConstraints().add(
						new DateRangeConstraint(item.getFieldName(), item
								.getDisplayName(),
								DateUtil.getDate(1800, 1, 1), DateUtil.getDate(
										3000, 12, 31)));
			}
			break;
		case String:
			if (item.getLength() > 0
					&& FieldConfigItem.TEXT.contains(item.getHtmlCtrl()
							.getHtmlCtrl()))
				getConstraints().add(
						new StringLengthConstraint(item.getFieldName(), item
								.getDisplayName(), item.getLength()));
			break;
		}
		if (!item.isEmpty()
				&& FieldConfigItem.EMPTY_CHECK.contains(item.getHtmlCtrl()
						.getHtmlCtrl()))
			getConstraints().add(
					new NotEmptyConstraint(item.getFieldName(), item
							.getDisplayName()));
		// if (item.Upload != null)
		// Constraints.Add(new InternalUploadConstraint(item.FieldName,
		// item.DisplayName, item.Upload));
	}

	protected void addConstraints(UpdateKind status) {
		// ConstraintArgs.SetProperties(this.Constraints, status);
		// if (AddConstraint != null)
		// AddConstraint(this, ConstraintArgs);
	}

	private void addDefaultValue(FieldConfigItem item) {
		if (item.getDefaultValue() != null)
			defaultValue.add(item);
	}

	private void addUpdating(FieldConfigItem item) {
		if (item.getUpdating() != null)
			updating.add(item);
	}

	private void addEasySearches(FieldConfigItem item) {
		if (!StringUtil.isEmpty(item.getEasySearch()))
			easySearchFields.add(item.getFieldName(), item.getDisplayName(),
					item.getEasySearch(), getHostDataSet(), this,
					!FieldConfigItem.DETAIL.contains(item.getHtmlCtrl()
							.getHtmlCtrl()));
	}

	public void addEasySearchFields(PageStyle style) {
	}

	public void addListSearches() {
	}

	private void addFieldSearches(FieldConfigItem item) {
		String fieldName = item.getFieldName();
		switch (item.getFieldDataType()) {
		case Date:
			if (!getListSearches().containsKey(fieldName)) {
				if (item.isSpan())
					DateSpanListSearch.add(getListSearches(), fieldName);
				else
					getListSearches().put(fieldName,
							SimpleDateListSearch.Search);
			}
			break;
		case DateTime:
			if (item.isSpan() && !!getListSearches().containsKey(fieldName))
				DateTimeSpanListSearch.add(getListSearches(), fieldName);
			break;
		case Int:
		case Double:
		case String:
			if (item.isSpan() && !getListSearches().containsKey(fieldName))
				SpanListSearch.add(listSearches, fieldName);
			break;
		}
	}

	public void addVirtualFields() {
		if (tempDataXml == null)
			return;
		DataTable table = getHostTable();
		if (table == null)
			return;
		if (tempDataXml.getTable().getVirtualItems().size() == 0)
			return;

		DataColumnCollection cols = table.getColumns();
		for (FieldConfigItem config : tempDataXml.getTable().getVirtualItems()) {
			switch (config.getKind()) {
			case Virtual:
				cols.add(config.getFieldName(), config.getDataType());
				break;
			case Calc:
				cols.add(config.getFieldName(), config.getDataType());
				break;
			}
		}
	}

	public final void checkConstraints(DataSet postDataSet) {
		getConstraints().check(postDataSet, getErrorObjects());
	}

	public final void checkError() {
		getErrorObjects().checkError();
	}

	public final void checkFirstConstraints(UpdateKind status,
			DataSet postDataSet) {
		addEasySearchFields(PageStyle.Insert);

		getConstraints(status);

		getConstraints().checkFirst(postDataSet, getErrorObjects());
	}

	public final ApplicationGlobal getAppGlobal() {
		return appGbl;
	}

	public final ConstraintCollection getConstraints() {
		if (constraints == null)
			constraints = new ConstraintCollection(getTableName(),
					getHostDataSet());
		return constraints;
	}

	public final boolean isAutoUpdating() {
		return autoUpdating;
	}

	public final void setAutoUpdating(boolean autoUpdating) {
		this.autoUpdating = autoUpdating;
	}

	public final ApplicationGlobal getAppGbl() {
		return appGbl;
	}

	public final SessionGlobal getSessionGbl() {
		return sessionGbl;
	}

	public final INonUIResolvers getNonUIResolvers() {
		return nonUIResolvers;
	}

	public final void getConstraints(UpdateKind status) {
		addConstraints(status);

		for (BaseEasySearchField field : easySearchFields) {
			BaseConstraint constraint = field.getConstraint();
			if (constraint != null)
				getConstraints().add(constraint);
		}
	}

	public final DataXml getDataXml() {
		return dataXml;
	}

	public final String getDisplayName(String fieldName) {
		IFieldInfo info = getFieldInfo(fieldName);
		return info == null ? fieldName : info.getDisplayName();
	}

	public final ErrorObjectCollection getErrorObjects() {
		if (errorObjects == null)
			errorObjects = new ErrorObjectCollection(getTableName());
		return errorObjects;
	}

	protected final ExpressionDataListener getExprListener() {
		if (exprListener == null)
			exprListener = new WebDataListener(this, getHostDataSet());
		return exprListener;
	}

	public final HashMap<String, BaseListSearch> getListSearches() {
		if (listSearches == null)
			listSearches = new HashMap<String, BaseListSearch>();
		return listSearches;
	}

	private final DataNavEventArgs getNaviArgs() {
		if (naviArgs == null)
			naviArgs = new DataNavEventArgs(this);
		return naviArgs;
	}

	final String getOwnerField() {
		return ownerField;
	}

	QueryParamCondition getQueryCondition(DataTable postTable) {
		if (postTable.getRows().size() == 0)
			throw new ToolkitException(String.format("表%s没有提交查询条件，无法查询",
					getTableName()));

		getListSearches().clear();
		addListSearches();

		ArrayList<DbDataParameter> queryParams = new ArrayList<DbDataParameter>();

		addEasySearchFields(PageStyle.List);
		hasAddEasySearches = true;

		DataRow row = postTable.getRows().getItem(0);
		for (BaseEasySearchField easySearch : easySearchFields) {
			((EasySearchField) easySearch).getItem().setQueryDataSet(postTable,
					row, easySearch.getFieldName());
		}

		BaseListSearch defaultSearch = DefaultListSearch.getInstance();
		boolean isEqual = "1".equals(row.getItem("query").toString());

		StringBuilder sqlCon = new StringBuilder();
		int i = 0;
		for (DataColumn column : postTable.getColumns()) {
			String columnName = column.getColumnName();
			if ("query".equals(columnName.toLowerCase()))
				continue;
			String fieldValue = row.getItem(column).toString();
			if ("~".equals(fieldValue)) {
				if (i++ != 0)
					sqlCon.append(" AND ");

				sqlCon.append(String.format("(%s IS NULL OR %s = '')",
						columnName, columnName));
			} else if (!"".equals(fieldValue)) {
				String condition = "";

				BaseListSearch colSearch = getListSearches().get(columnName);
				if (colSearch == null)
					colSearch = defaultSearch;
				colSearch.setProperties(row, isEqual);
				switch (colSearch.getSqlType()) {
				case Param:
					IParamBuilder builder = colSearch.getParamCondition(
							getFieldInfo(columnName).getDataType(), columnName,
							fieldValue);
					condition = builder.getSQL();
					for (DbDataParameter param : builder.getParams())
						queryParams.add(param);
					break;
				case String:
					condition = colSearch.getCondition(columnName, fieldValue);
					break;
				}
				if (StringUtil.isEmpty(condition))
					continue;
				if (i++ != 0)
					sqlCon.append(" AND ");
				sqlCon.append(condition);

			}
		}
		return new QueryParamCondition(sqlCon.toString(), queryParams);
	}

	public final CodeTableCollection getRegCodeTables() {
		if (regCodeTables == null)
			regCodeTables = new CodeTableCollection();
		return regCodeTables;
	}

	public final HttpServletRequest getRequest() {
		return request;
	}

	public final HttpServletResponse getResponse() {
		return response;
	}

	final String getRightType() {
		return rightType;
	}

	public final SessionGlobal getSessionGlobal() {
		return sessionGbl;
	}

	protected void onBeginNavigateData(EventArgs e) {
		if (naviDataListener != null)
			naviDataListener.beginNavigateData(e);
	}

	protected void onEndNavigateData(EventArgs e) {
		if (naviDataListener != null)
			naviDataListener.endNavigateData(e);
	}

	protected void onNavigatingData(DataNavEventArgs e) {
		if (naviDataListener != null)
			naviDataListener.navigatingData(e);
	}

	private static <E extends Enum<E>> void mixEnumSet(EnumSet<E> enumSet,
			E enumItem) {
		boolean isContain = enumSet.contains(enumItem);
		enumSet.clear();
		if (isContain)
			enumSet.add(enumItem);
	}

	@Override
	protected void onSetFieldInfo(FieldInfoEventArgs e) {
		super.onSetFieldInfo(e);

		if (dataXml.getTable().isContainBlob()
				&& e.getStatus() == UpdateKind.Update
				&& dataXml.getTable().isKey(e.getFieldInfo().getColumnName()))
			mixEnumSet(e.getFieldInfo().getSQLFlag(), SQLFlag.Where);
		if (isAutoInc
				&& dataXml.getTable().getAutoIncItems().containsKey(
						e.getFieldInfo().getColumnName()))
			mixEnumSet(e.getFieldInfo().getSQLFlag(), SQLFlag.Where); // 如果Where标记有则Where存在，否则即为None
	}

	@Override
	protected void onSettingField(FieldEventArgs e) {
		super.onSettingField(e);

		if (isAutoInc
				&& dataXml.getTable().getAutoIncItems().containsKey(
						e.getColName()))
			e.setHandled(true);
	}

	@Override
	protected void onUpdatingRow(UpdatingEventArgs e) {
		super.onUpdatingRow(e);

		if (autoUpdating) {
			EnumSet<UpdateKindType> enumSet = null;
			switch (e.getStatus()) {
			case Insert:
				enumSet = INSERT_SET;
				break;
			case Update:
				enumSet = UPDATE_SET;
				break;
			}
			e.getRow().beginEdit();
			for (FieldConfigItem item : updating) {
				if (enumSet.contains(item.getUpdating().getUpdateKind()))
					e.getRow().setItem(
							item.getFieldName(),
							item.getUpdating().toString(
									getAppGlobal().getRegsCollection(),
									getExprListener()));
			}
			e.getRow().endEdit();
		}
	}

	public void mergeErrorTable(DataTable dstTable, DataTable postTable,
			DataTable fillTable) {
		for (DataRow row : postTable.getRows()) {
			DataRow newRow = dstTable.newRow();
			for (String key : getKeyFieldArray()) {
				if (postTable.getColumns().contains(key))
					newRow.setItem(key, row.getItem(key));
				else
					throw new ToolkitException(String.format(
							"表%s提交的数据没有包含主键的信息。", getTableName()));
			}
			dstTable.getRows().add(newRow);
		}

		boolean isPrimary = true;
		try {
			setPrimaryKeys(dstTable);
		} catch (Exception ex) {
			isPrimary = false;
		}

		for (DataRow fillRow : fillTable.getRows()) {
			DataRow srcRow;
			if (isPrimary)
				srcRow = findRow(dstTable, fillRow);
			else {
				srcRow = null;
				for (DataRow row : dstTable.getRows())
					if (getKeyCount() == 1) {
						if (row.getItem(getKeyFields()).toString().equals(
								fillRow.getItem(getKeyFields()).toString())) {
							srcRow = row;
							break;
						}
					} else {
						boolean allMatch = true;
						for (String key : getKeyFieldArray())
							if (row.getItem(key).toString().equals(
									fillRow.getItem(key).toString()))
								allMatch = false;
						if (allMatch) {
							srcRow = row;
							break;
						}
					}
			}
			if (srcRow != null) {
				for (DataColumn col : dstTable.getColumns()) {
					String colName = col.getColumnName();
					if ("".equals(srcRow.getItem(colName).toString())
							&& fillTable.getColumns().contains(colName))

						srcRow.setItem(colName, fillRow.getItem(colName));
				}
			}
		}

		for (int i = 0; i < postTable.getRows().size(); ++i) {
			DataRow newRow = dstTable.getRows().getItem(i);
			DataRow row = postTable.getRows().getItem(i);
			for (DataColumn col : postTable.getColumns()) {
				if (dstTable.getColumns().contains(col.getColumnName()))
					newRow.setItem(col.getColumnName(), row.getItem(col));
			}
		}
	}

	public void navigateData(PageStyle style) {
		DataTable table = getHostTable();
		if (table == null || table.getRows().size() == 0)
			return;

		if (!hasAddEasySearches)
			if (style == PageStyle.List || style == PageStyle.Detail
					|| "GET".equals(getRequest().getMethod().toUpperCase()))
				addEasySearchFields(style);

		EventArgs e = new EventArgs(this);
		onBeginNavigateData(e);
		for (DataRow row : table.getRows()) {
			getNaviArgs().setRow(row);
			onNavigatingData(getNaviArgs());
		}
		onEndNavigateData(e);
	}

	public final void prepareDataSet(DataSet postDataSet) {
		DataTable table = postDataSet.getTables().getItem(getTableName());
		if (table != null) {
			for (int i = table.getRows().size() - 1; i >= 0; --i) {
				DataRow row = table.getRows().getItem(i);

				// 删除空行
				boolean isEmpty = true;
				for (int j = 0; j < table.getColumns().size(); ++j)
					if (!"".equals(row.getItem(j).toString())) {
						isEmpty = false;
						break;
					}
				if (isEmpty) {
					table.getRows().removeAt(i);
					continue;
				}

				// 新增行设置负数为主键
				if (getKeyCount() == 1) {
					if ("".equals(row.getItem(getKeyFields()).toString()))
						row.setItem(getKeyFields(), -i);
				} else {
					for (String key : getKeyFieldArray())
						if ("".equals(row.getItem(key).toString()))
							row.setItem(key, -i);
				}
			}
		}
	}

	public void setData(HttpServletRequest request,
			HttpServletResponse response, ApplicationGlobal appGbl,
			SessionGlobal sessionGbl) {
		this.request = request;
		this.response = response;
		this.appGbl = appGbl;
		this.sessionGbl = sessionGbl;
	}

	@Override
	public void setDefaultValue(DataRow row) {
		if (defaultValue == null)
			return;
		for (FieldConfigItem item : defaultValue) {
			try {
				row.setItem(item.getFieldName(), item.getDefaultValue()
						.toString(getAppGbl().getRegsCollection(),
								getExprListener()));
			} catch (Exception ex) {
			}
		}
	}

	protected final void setDataXml(DataXml dataXml) {
		this.dataXml = dataXml;
		setConfigXml(dataXml.getTable());
		this.isAutoInc = dataXml.getTable().getAutoIncItems() != null
				&& dataXml.getTable().getAutoIncItems().size() > 0;
	}

	public void setDocument(PageStyle style, boolean isPost, Node node) {
		tempDataXml = DataXml.parseTempXml(node, dataXml.getTable()
				.getTableName());
		switch (style) {
		case Insert:
			if (!isPost) {
				if (defaultValue == null)
					defaultValue = new ArrayList<FieldConfigItem>();
				else
					defaultValue.clear();
			}
		case Update:
			if (isPost) {
				if (updating == null)
					updating = new ArrayList<FieldConfigItem>();
				else
					updating.clear();
			}
			break;
		case Detail:
			break;
		case List:
			if (isPost) {
				if (listSearches == null)
					listSearches = new HashMap<String, BaseListSearch>();
				else
					listSearches.clear();
			}
			break;
		}

		configCodeTables.clear();

		for (FieldConfigItem item : tempDataXml.getTable().getField()) {
			addCodeTables(item, style);
			addEasySearches(item);
			switch (style) {
			case Insert:
				if (!isPost)
					addDefaultValue(item);
			case Update:
				addConstraints(item);
				if (isPost)
					addUpdating(item);
				break;
			case Detail:
				break;
			case List:
				if (isPost)
					addFieldSearches(item);
				break;
			}
		}
	}

	@Override
	public void setHostDataSet(BaseDataSet hostDataSet) {
		super.setHostDataSet(hostDataSet);
		if (hostDataSet instanceof INonUIResolvers)
			nonUIResolvers = (INonUIResolvers) hostDataSet;
	}

	final void setOwnerField(String ownerField) {
		this.ownerField = ownerField;
	}

	void setRegCodeTables(PageStyle style) {
		getRegCodeTables().clear();
		addCodeTables(getRegCodeTables(), style);
	}

	void setRightArgs(DataRightEventArgs e) {
		e.setOwnerField(this.getOwnerField());
		e.setRightType(this.getRightType());
	}

	void setRightArgs(ResolverConfigItem item) {
		this.setOwnerField(item.getOwnerField());
		this.setRightType(item.getRightType());
	}

	final void setRightType(String rightType) {
		this.rightType = rightType;
	}

	private DataTable getCheckRightTable(String ownerField) {
		DataTable table = getHostTable();
		if (table == null)
			return null;
		if (AppSetting.getCurrent().isDebug()
				&& !table.getColumns().contains(ownerField))
			throw new ToolkitException(String.format(
					"数据权限错误，表%s并不包含字段名%s，请检查。", getTableName(), ownerField));
		return table;
	}

	public void checkPublic(IDataRight right, Object type, String ownerField) {
		DataTable table = getCheckRightTable(ownerField);
		if (table == null)
			return;
		for (DataRow row : table.getRows())
			right.checkPublic(type, row.getItem(ownerField));
	}

	public void checkReadWrite(IDataRight right, Object type, String ownerField) {
		DataTable table = getCheckRightTable(ownerField);
		if (table == null)
			return;
		for (DataRow row : table.getRows())
			right.checkReadWrite(type, row.getItem(ownerField));
	}

	public void checkDelete(IDataRight right, Object type, String ownerField) {
		DataTable table = getCheckRightTable(ownerField);
		if (table == null)
			return;
		for (DataRow row : table.getRows())
			right.checkDelete(type, row.getItem(ownerField));
	}

	public synchronized void addNavigateDataListener(NavigateDataListener l) {
		naviDataListener = NavigateDataEventMulticaster
				.add(naviDataListener, l);
	}

	public synchronized void removeNavigateDataListener(NavigateDataListener l) {
		naviDataListener = NavigateDataEventMulticaster.remove(
				naviDataListener, l);
	}
}
