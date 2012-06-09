package edu.hziee.common.xslt2web.data;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.configxml.MarcoConfigItem;
import edu.hziee.common.xslt2web.easysearch.CodeTable;
import edu.hziee.common.xslt2web.right.DataRightEventArgs;
import edu.hziee.common.xslt2web.right.FunctionRightType;
import edu.hziee.common.xslt2web.right.IDataRight;
import edu.hziee.common.xslt2web.right.ISupportFunction;
import edu.hziee.common.xslt2web.sys.AppSetting;
import edu.hziee.common.xslt2web.sys.ApplicationGlobal;
import edu.hziee.common.xslt2web.sys.ExpressionDataListener;
import edu.hziee.common.xslt2web.sys.IListPage;
import edu.hziee.common.xslt2web.sys.INeedWebData;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sys.IWebData;
import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sys.QueryParamCondition;
import edu.hziee.common.xslt2web.sys.SessionGlobal;
import edu.hziee.common.xslt2web.sys.UserInfo;
import edu.hziee.common.xslt2web.sys.WebDataListener;
import edu.hziee.common.xslt2web.sysutil.FileUtil;
import edu.hziee.common.xslt2web.sysutil.StringUtil;
import edu.hziee.common.xslt2web.sysutil.XslTransformUtil;
import edu.hziee.common.xslt2web.xml.IDoubleTransform;

public abstract class WebListDataSet extends BaseDataSet implements IListPage,
		ISupportFunction, IDoubleTransform, IListEvent, IMainTableResolver,
		INeedWebData, IWebData, AdapterAddedListener<AbstractXmlTableResolver> {
	private static final String DEBUG_FILE = FileUtil.combin(AppSetting
			.getCurrent().getXmlPath(), "Tk2Template\\Debug.xslt");

	private static final String TEMP_FILE = FileUtil.combin(AppSetting
			.getCurrent().getXmlPath(), "Temp.xml");

	private AbstractXmlTableResolver mainResolver;
	private HashMap<String, CodeTable> codeTables;
	private DbCommand command;

	private HashMap<String, String> ignoreParams;
	private String defaultOrder = "DESC";
	private int defaultSortField;
	private boolean sortQuery;
	private int pageSize;
	private boolean needQuery;

	private Node dataXmlDocument;
	private String queryCondition;
	private String listFields = "";
	private String[] listFieldNames;
	private boolean hasSetDocument;
	private MarcoConfigItem filterSQL;

	private FunctionRightType funcType;
	private Object functionKey;
	private boolean supportData;
	private IDataRight dataRight;

	private HttpServletRequest request;
	private HttpServletResponse response;
	private ApplicationGlobal appGbl;
	private SessionGlobal sessionGbl;

	private IModuleProvider moduleProvider;
	private ExpressionDataListener dataListener;
	private FilledCustomListener filledCustomListener;
	private FilledListListener filledListListener;

	public WebListDataSet() {
	}

	public HashMap<String, String> getIgnoreParams() {
		if (ignoreParams == null)
			ignoreParams = DataSetOperUtil.getIngoreParams();
		return ignoreParams;
	}

	public String getQueryCondition(DataSet postDataSet) {
		return queryCondition;
	}

	public String getAlertString(PageStyle style, String operation,
			HttpServletRequest request, DataSet postDataSet) {
		return moduleProvider == null ? "" : moduleProvider.getAlertString(
				style, operation, request, postDataSet);
	}

	public String getJScript(PageStyle style, String operation) {
		return "";
	}

	public String post(PageStyle style, String operation,
			HttpServletRequest request, DataSet postDataSet) {
		setData(true, style, operation, request, postDataSet);

		return "";
	}

	public String getDefaultPage(boolean isPost, PageStyle style,
			String operation, String retURL) {
		if (!StringUtil.isEmpty(retURL))
			return retURL;
		DataSetAnnotation attr = getAnnotation();
		if (attr == null)
			return "";
		else
			return String.format("../toolkit/weblistpage?Source=%s", attr
					.regName());
	}

	public String getXsltFile(boolean isIe, PageStyle style, String operation) {
		return moduleProvider == null ? null : moduleProvider.getXsltFile(isIe,
				style, operation);
	}

	public void setData(boolean isPost, PageStyle style, String operation,
			HttpServletRequest request) {
		setData(isPost, style, operation, request, null);
	}

	public final FunctionRightType getFuncType() {
		return funcType;
	}

	public final void setFuncType(FunctionRightType funcType) {
		this.funcType = funcType;
	}

	public final void setFunctionKey(Object functionKey) {
		this.functionKey = functionKey;
	}

	public final Object getFunctionKey() {
		return functionKey;
	}

	public Object getSubFunctionKey(PageStyle style, String operation) {
		return moduleProvider == null ? null : moduleProvider
				.getSubFunctionKey(style, operation);
	}

	public String getDefaultXsltTemplate(PageStyle style, String operation) {
		String defaultResult = DataSetOperUtil.getDefaultXsltTemplate(style,
				operation);
		if (moduleProvider != null) {
			String xslt = moduleProvider.getDefaultXsltTemplate(style,
					operation);
			return "".equals(xslt) ? defaultResult : xslt;
		} else
			return defaultResult;
	}

	public int getDocumentNumber(PageStyle style, String operation) {
		return 1;
	}

	public boolean getSupportDoubleTransform(PageStyle style, String operation) {
		return true;
	}

	public Node getXmlDocument(int i, PageStyle style, String operation) {
		return dataXmlDocument;
	}

	public final synchronized void addFilledCustomListener(FilledCustomListener l) {
		filledCustomListener = FilledCustomEventMulticaster.add(
				filledCustomListener, l);
	}

	public final synchronized void addFilledListListener(FilledListListener l) {
		filledListListener = FilledListEventMulticaster.add(filledListListener,
				l);
	}

	public final synchronized void removeFilledCustomListener(FilledCustomListener l) {
		filledCustomListener = FilledCustomEventMulticaster.remove(
				filledCustomListener, l);
	}

	public final synchronized void removeFilledListListener(FilledListListener l) {
		filledListListener = FilledListEventMulticaster.remove(
				filledListListener, l);
	}

	public final AbstractXmlTableResolver getMainResolver() {
		return mainResolver;
	}

	public final ApplicationGlobal getAppGlobal() {
		return appGbl;
	}

	protected final HashMap<String, CodeTable> getCodeTables() {
		if (codeTables == null)
			codeTables = new HashMap<String, CodeTable>();
		return codeTables;
	}

	protected final DbCommand getCommand() {
		if (command == null)
			command = getConnection().createCommand();
		return command;
	}

	public final HttpServletRequest getRequest() {
		return request;
	}

	public final HttpServletResponse getResponse() {
		return response;
	}

	public final SessionGlobal getSessionGlobal() {
		return sessionGbl;
	}

	public void setData(HttpServletRequest request,
			HttpServletResponse response, ApplicationGlobal appGlobal,
			SessionGlobal ssnGlobal) {
		this.request = request;
		this.response = response;
		this.appGbl = appGlobal;
		this.sessionGbl = ssnGlobal;
		setResolvers();
	}

	protected final void setDataXmlDocument(PageStyle style, String operation,
			boolean isPost) {
		if (hasSetDocument)
			return;

		Document xmlDoc = getModuleXml();

		// DoubleXmlDocumentItem item =
		// DoubleXmlDocumentItem.GetDocumentItem(xmlDoc.BaseURI, style,
		// operation);
		// if (item.Document == null)
		// {
		String dataXslt = getDataXslt(style, operation);
		if (StringUtil.isEmpty(dataXslt))
			return;
		String xslFile = FileUtil.combin(AppSetting.getCurrent().getXmlPath(),
				dataXslt);
		dataXmlDocument = XslTransformUtil.transform2Node(xmlDoc, xslFile);
		if (AppSetting.getCurrent().isDebug())
			XslTransformUtil.transform2File(dataXmlDocument, DEBUG_FILE,
					TEMP_FILE);
		// fDataXmlDocument = new XPathDocument(new
		// StringReader(xml)).CreateNavigator();
		// item.Document = fDataXmlDocument;
		// }
		// else
		// fDataXmlDocument = item.Document;
		internalSetDataXmlDocument(style, operation, isPost, dataXmlDocument);
		if (style == PageStyle.List && "".equals(listFields)) {
			listFieldNames = DataSetOperUtil.getOrderFields(dataXmlDocument);
			listFields = DataSetOperUtil.getListFields(dataXmlDocument);
		}
		hasSetDocument = true;
	}

	private void setListData(boolean isPost, HttpServletRequest request,
			DataSet postDataSet) {
		int sortField = 0;
		String fieldName = "";

		if (!isPost) {
			String sort = request.getParameter("Sort");
			sortField = (StringUtil.isEmpty(sort)) ? getDefaultSortField()
					: StringUtil.getDefaultInt(sort, 0);
			fieldName = getOrderFieldName(sortField - 1);
		} else {
			if (sortQuery) {
				String sort = request.getParameter("Sort");
				sortField = (StringUtil.isEmpty(sort)) ? 0 : StringUtil
						.getDefaultInt(sort, 0);
				if (sortField > 0)
					fieldName = getOrderFieldName(sortField - 1);
			}
		}
		Object[] data = DataSetOperUtil.SetListData(this, isPost, request,
				postDataSet, getListView(), sortField, getDefaultOrder(),
				fieldName, sortQuery);
		DataRow sortRow = (DataRow) data[0];
		queryCondition = (String) data[1];
		QueryParamCondition condition = (QueryParamCondition) data[2];
		int count = (Integer) data[3];
		int pageNumber = (Integer) data[4];
		String order = (String) data[5];
		String tab = (String) data[6];

		setListCondition(condition);

		if (filterSQL != null)
			condition.addSQL(filterSQL.toString(getAppGlobal()
					.getRegsCollection(), getDataListener()));
		String tabCondition = getTabSheetCondition(tab);
		if (!"".equals(tabCondition))
			condition.addSQL(tabCondition);

		IParamBuilder dataRight = getPublic();

		if (dataRight != null)
			condition.addSQL(dataRight);

		count = fillListTable(getListView(), condition, order, pageNumber,
				getListPageSize(), count, isPost, postDataSet);
		sortRow.setItem("Count", count);

		navigateData(PageStyle.List);
		processCodeTables(PageStyle.List);
	}

	private void setData(boolean isPost, PageStyle style, String operation,
			HttpServletRequest request, DataSet postDataSet) {
		switch (style) {
		case List:
			setDataXmlDocument(style, operation, isPost);
			if (!isPost)
				setListData(false, request, null);
			else
				setListData(true, request, postDataSet);
			break;
		}
	}

	protected void setResolvers() {
	}

	protected abstract Document getModuleXml();

	protected String getDataXslt(PageStyle style, String operation) {
		String defaultResult = DataSetOperUtil.getDataXslt(style, operation);
		if (moduleProvider != null) {
			String xslt = moduleProvider.getDataXslt(style, operation);
			return "".equals(xslt) ? defaultResult : xslt;
		} else
			return defaultResult;
	}

	protected void internalSetDataXmlDocument(PageStyle style,
			String operation, boolean isPost, Node data) {
		mainResolver.setDocument(style, isPost, data);
	}

	public final AbstractXmlTableResolver getListView() {
		return mainResolver;
	}

	public final String getDefaultOrder() {
		return defaultOrder;
	}

	public final void setDefaultOrder(String defaultOrder) {
		this.defaultOrder = defaultOrder;
	}

	public final int getDefaultSortField() {
		return defaultSortField;
	}

	public final void setDefaultSortField(int defaultSortField) {
		this.defaultSortField = defaultSortField;
	}

	public final boolean isSortQuery() {
		return sortQuery;
	}

	public final void setSortQuery(boolean sortQuery) {
		this.sortQuery = sortQuery;
	}

	public final ExpressionDataListener getDataListener() {
		if (dataListener == null)
			dataListener = new WebDataListener(this, this);
		return dataListener;
	}

	public final int getPageSize() {
		return pageSize;
	}

	public final void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public final boolean isNeedQuery() {
		return needQuery;
	}

	public final void setNeedQuery(boolean needQuery) {
		this.needQuery = needQuery;
	}

	public final MarcoConfigItem getFilterSQL() {
		return filterSQL;
	}

	public final void setFilterSQL(MarcoConfigItem filterSQL) {
		this.filterSQL = filterSQL;
	}

	public final boolean isSupportData() {
		return supportData;
	}

	public final void setSupportData(boolean supportData) {
		this.supportData = supportData;
	}

	public final String getListFields() {
		return listFields;
	}

	public final IModuleProvider getModuleProvider() {
		return moduleProvider;
	}

	public final void setModuleProvider(IModuleProvider moduleProvider) {
		this.moduleProvider = moduleProvider;
	}

	public final UserInfo getInfo() {
		return sessionGbl.getInfo();
	}

	public final IDataRight getDataRight() {
		if (dataRight == null)
			dataRight = sessionGbl.getRights().getDataRight();
		return dataRight;
	}

	protected final void setMainResolver(AbstractXmlTableResolver mainResolver) {
		this.mainResolver = mainResolver;
		setTableResolverData(mainResolver);
	}

	protected int getListPageSize() {
		return pageSize == 0 ? sessionGbl.getInfo().getPageSize()
				: getPageSize();
	}

	protected String getOrderFieldName(int index) {
		String fields = getListFields();
		if (StringUtil.isEmpty(fields))
			return "";
		return StringUtil.getListField(listFieldNames, index);
	}

	protected void processCodeTables(PageStyle style) {
		ResolverUtil.processRegCodeTables(getMainResolver(), getCodeTables(),
				getCommand(), style);
	}

	protected void navigateData(PageStyle style) {
		getMainResolver().navigateData(style);
	}

	protected void setListCondition(QueryParamCondition condition) {
	}

	protected String getTabSheetCondition(String tab) {
		return moduleProvider == null ? "" : moduleProvider
				.getTabSheetCondition(tab);
	}

	protected final IParamBuilder getPublic() {
		if (supportData) {
			DataRightEventArgs e = new DataRightEventArgs(this);
			setRightProperties(getListView(), e);
			if (!e.isChecked())
				return getDataRight().getPublicSql(e.getRightType(),
						e.getOwnerField().toString(), getInfo().getUserID());
		}
		return null;
	}

	protected void setRightProperties(AbstractXmlTableResolver resolver,
			DataRightEventArgs e) {
		e.setProperty(resolver.getTableName());
		resolver.setRightArgs(e);
	}

	protected int fillListTable(AbstractXmlTableResolver listView,
			QueryParamCondition sqlCon, String order, int pageNumber,
			int pageSize, int count, boolean isPost, DataSet postDataSet) {
		int totalCount = DataSetOperUtil.fillListTable(this, listView, sqlCon,
				order, pageNumber, pageSize, count, getListFields());

		onFilledListTables(FilledListEventArgs.getArgs(this, listView, sqlCon,
				order, pageNumber, pageSize, totalCount, isPost, postDataSet));

		return totalCount;
	}

	protected void onFilledCustomTables(FilledCustomEventArgs e) {
		if (filledCustomListener != null)
			filledCustomListener.filledCustomTables(e);
	}

	protected void onFilledListTables(FilledListEventArgs e) {
		if (filledListListener != null)
			filledListListener.filledListTables(e);
	}

	protected final void setTableResolverData(AbstractXmlTableResolver resolver) {
		if (resolver != null)
			resolver.setData(request, response, appGbl, sessionGbl);
	}

	public final void addAdapter(AdapterAddedEventArgs<AbstractXmlTableResolver> e) {
		setTableResolverData(e.getAdapter());
	}
}
