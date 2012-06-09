package edu.hziee.common.xslt2web.data;

import java.util.EnumSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.configxml.MarcoConfigItem;
import edu.hziee.common.xslt2web.easysearch.CodeTable;
import edu.hziee.common.xslt2web.exception.ErrorPageException;
import edu.hziee.common.xslt2web.exception.InformationException;
import edu.hziee.common.xslt2web.right.DataRightEventArgs;
import edu.hziee.common.xslt2web.right.FunctionRightType;
import edu.hziee.common.xslt2web.right.IDataRight;
import edu.hziee.common.xslt2web.right.ISupportFunction;
import edu.hziee.common.xslt2web.sys.AppSetting;
import edu.hziee.common.xslt2web.sys.ApplicationGlobal;
import edu.hziee.common.xslt2web.sys.ExpressionDataListener;
import edu.hziee.common.xslt2web.sys.IDeletePage;
import edu.hziee.common.xslt2web.sys.IDetailPage;
import edu.hziee.common.xslt2web.sys.IInsertPage;
import edu.hziee.common.xslt2web.sys.IListPage;
import edu.hziee.common.xslt2web.sys.INeedWebData;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sys.IUpdatePage;
import edu.hziee.common.xslt2web.sys.IWebData;
import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sys.QueryParamCondition;
import edu.hziee.common.xslt2web.sys.SessionGlobal;
import edu.hziee.common.xslt2web.sys.UserInfo;
import edu.hziee.common.xslt2web.sys.WebDataListener;
import edu.hziee.common.xslt2web.sysutil.DataSetCopyUtil;
import edu.hziee.common.xslt2web.sysutil.DataSetUtil;
import edu.hziee.common.xslt2web.sysutil.FileUtil;
import edu.hziee.common.xslt2web.sysutil.StringUtil;
import edu.hziee.common.xslt2web.sysutil.XslTransformUtil;
import edu.hziee.common.xslt2web.xml.IDoubleTransform;

public abstract class WebUpdateDataSet extends UpdateDataSet implements
		IDetailPage, IInsertPage, IUpdatePage, IListPage, IDeletePage,
		ISupportFunction, IDoubleTransform, INeedWebData, IWebData,
		AdapterAddedListener<AbstractXmlTableResolver>, IListEvent,
		IListDetailEvent, IUpdateEvent {

	public class DisableFuncException extends ErrorPageException {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String[] STYLE_STRS = { "", // custom
				"新建", // insert
				"修改", // update
				"删除", // delete
				"详细", // detail
				"列表" // list
		};

		public DisableFuncException(String name, int style) {
			super();
			String errorMsg = String.format("模块%s的%s功能被禁止了。", name,
					STYLE_STRS[style]);
			setErrorBody(errorMsg);
			setErrorTitle("功能被禁止");
			setPageTitle("非法操作");
		}
	}

	private static final String DEBUG_FILE = FileUtil.combin(AppSetting
			.getCurrent().getXmlPath(), "Tk2Template\\Debug.xslt");

	private static final String TEMP_FILE = FileUtil.combin(AppSetting
			.getCurrent().getXmlPath(), "Temp.xml");

	private HashMap<String, String> ignoreParams;

	private String defaultOrder = "DESC";

	private int defaultSortField;

	private boolean sortQuery;

	private int pageSize;

	private HashMap<String, CodeTable> codeTables;

	private DbCommand command;

	// private TableSelector selector;
	private boolean queryStringInput = true;

	private EnumSet<DisableFunction> disablePage;

	private String saveMethod;

	private boolean supportData;

	private IDataRight dataRight;

	private FunctionRightType funcType = FunctionRightType.None;

	private Object functionKey;

	private Node dataXmlDocument;

	private String queryCondition;

	private String listFields = "";

	private String[] listFieldNames;

	private boolean hasSetDocument = false;

	private MarcoConfigItem filterSQL;

	private HttpServletRequest request;

	private HttpServletResponse response;

	private ApplicationGlobal appGbl;

	private SessionGlobal sessionGbl;

	private IModuleProvider moduleProvider;

	private FillingUpdateEventArgs fillingUpdateArgs;

	private FillingDetailListEventArgs fillingDetailListArgs;

	private ExpressionDataListener dataListener;

	private FilledCustomListener filledCustomListener;

	private FilledListListener filledListListener;

	private FillingUpdateListener fillingUpdateListener;

	private FilledUpdateListener filledUpdateListener;

	private FillingDetailListListener fillingDetailListListener;

	private FilledDetailListListener filledDetailListListener;

	private FilledInsertListener filledInsertListener;

	private CommittingDataListener committingDataListener;

	private CommittedDataListener committedDataListener;

	protected WebUpdateDataSet() {

	}

	protected void checkDelete(DataRightEventArgs e) {
	}

	protected void checkPublic(DataRightEventArgs e) {
	}

	protected void checkReadOnly(DataRightEventArgs e) {
	}

	protected void checkReadWrite(DataRightEventArgs e) {
	}

	void commit() {
		commitData();
	}

	protected abstract void commitData();

	protected void committedData() {
		if (committedDataListener != null)
			committedDataListener.committedData(CommittedDataEventArgs
					.getArgs(this));
	}

	protected void committingData() {
		if (committingDataListener != null)
			committingDataListener.committingData(CommittingDataEventArgs
					.getArgs(this));
	}

	protected abstract void delete();

	protected void fillCustomTables(boolean isPost, String operation,
			HttpServletRequest request, DataSet postDataSet) {
	}

	protected void fillDetailListTables() {
	}

	protected final void fillInsertTables(boolean isPost,
			AbstractXmlTableResolver... resolvers) {
		if (!isPost) {
			for (AbstractXmlTableResolver resolver : resolvers) {
				resolver.createVirtualTable();
				resolver.getNewRow();
				resolver.addVirtualFields();
				resolver.getConstraints(UpdateKind.Insert);
			}
		} else {
			for (AbstractXmlDataAdapter resolver : resolvers)
				resolver.selectTableStructure();
		}
	}

	protected final void fillInsertTables(boolean isPost,
			TableAdapterCollection<AbstractXmlTableResolver> resolvers) {
		fillInsertTables(isPost, (AbstractXmlTableResolver[]) resolvers
				.toArray());
	}

	protected abstract void fillInsertTables(boolean isPost,
			HttpServletRequest request, DataSet postDataSet);

	protected int fillListTable(AbstractXmlTableResolver listView,
			QueryParamCondition sqlCon, String order, int pageNumber,
			int pageSize, int count, boolean isPost, DataSet postDataSet) {
		int totalCount = DataSetOperUtil.fillListTable(this, listView, sqlCon,
				order, pageNumber, pageSize, count, getListFields());

		onFilledListTables(FilledListEventArgs.getArgs(this, listView, sqlCon,
				order, pageNumber, pageSize, totalCount, isPost, postDataSet));

		return totalCount;
	}

	protected abstract void fillUpdateTables(HttpServletRequest request,
			DataSet postDataSet);

	protected abstract void fillUpdateTables(String key, PageStyle style,
			HttpServletRequest request);

	protected final void fillingUpdateEvent(boolean isPost, Object key,
			PageStyle style, HttpServletRequest request, DataSet postDataSet) {
		fillingUpdateArgs = FillingUpdateEventArgs.getArgs(this, isPost, key,
				style, request, postDataSet);
		addFillingUpdateTables(fillingUpdateArgs);
		onFillingUpdateTables(fillingUpdateArgs);
	}

	protected final void fillingDetailListEvent() {
		fillingDetailListArgs = FillingDetailListEventArgs.getArgs(this);
		addFillingDetailListTables(fillingDetailListArgs);
		onFillingDetailListTables(fillingDetailListArgs);
	}

	protected void addFillingUpdateTables(FillingUpdateEventArgs e) {
	}

	protected void addFillingDetailListTables(FillingDetailListEventArgs e) {
	}

	public String getAlertString(PageStyle style, String operation,
			HttpServletRequest request, DataSet postDataSet) {
		return moduleProvider == null ? "" : moduleProvider.getAlertString(
				style, operation, request, postDataSet);
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

	public final ExpressionDataListener getDataListener() {
		if (dataListener == null)
			dataListener = new WebDataListener(this, this);
		return dataListener;
	}

	public final IDataRight getDataRight() {
		if (dataRight == null)
			dataRight = sessionGbl.getRights().getDataRight();
		return dataRight;
	}

	protected String getDataXslt(PageStyle style, String operation) {
		String defaultResult = DataSetOperUtil.getDataXslt(style, operation);
		if (moduleProvider != null) {
			String xslt = moduleProvider.getDataXslt(style, operation);
			return "".equals(xslt) ? defaultResult : xslt;
		} else
			return defaultResult;
	}

	public final String getDefaultOrder() {
		return defaultOrder;
	}

	public String getDefaultPage(boolean isPost, PageStyle style,
			String operation, String retURL) {
		if (!StringUtil.isEmpty(retURL))
			return retURL;
		DataSetAnnotation attr = getAnnotation();
		if (attr == null)
			return "";
		if (!isPost)
			return String.format("../toolkit/weblistpage?Source=%s", attr
					.regName());
		else {
			if (this instanceof IMainTableResolver)
				return ResolverUtil.getDefault2URL(((IMainTableResolver) this)
						.getMainResolver(), getSaveMethod(), attr.regName(),
						style);
			else
				return String.format("../toolkit/weblistpage?Source=%s", attr
						.regName());
		}
	}

	public final int getDefaultSortField() {
		return defaultSortField;
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

	public final EnumSet<DisableFunction> getDisablePage() {
		return disablePage;
	}

	public int getDocumentNumber(PageStyle style, String operation) {
		return 1;
	}

	public final MarcoConfigItem getFilterSQL() {
		return filterSQL;
	}

	public final Object getFunctionKey() {
		return functionKey;
	}

	public final FunctionRightType getFuncType() {
		return funcType;
	}

	public HashMap<String, String> getIgnoreParams() {
		if (ignoreParams == null)
			ignoreParams = DataSetOperUtil.getIngoreParams();
		return ignoreParams;
	}

	public final UserInfo getInfo() {
		return sessionGbl.getInfo();
	}

	public abstract String getJScript(PageStyle style, String operation);

	public final String getListFields() {
		return listFields;
	}

	protected final int getListPageSize() {
		return pageSize == 0 ? getInfo().getPageSize() : getPageSize();
	}

	public abstract AbstractXmlTableResolver getListView();

	protected final FillingUpdateEventArgs getFillingUpdateArgs() {
		return fillingUpdateArgs;
	}

	protected final FillingDetailListEventArgs getFillingDetailListArgs() {
		return fillingDetailListArgs;
	}

	public final IModuleProvider getModuleProvider() {
		return moduleProvider;
	}

	protected abstract Document getModuleXml();

	protected String getOrderFieldName(int index) {
		String fields = getListFields();
		if (StringUtil.isEmpty(fields))
			return "";
		return StringUtil.getListField(listFieldNames, index);
	}

	public final int getPageSize() {
		return pageSize;
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

	public String getQueryCondition(DataSet postDataSet) {
		return queryCondition;
	}

	public final HttpServletRequest getRequest() {
		return request;
	}

	public final HttpServletResponse getResponse() {
		return response;
	}

	public final String getSaveMethod() {
		if ("get".equals(request.getMethod().toLowerCase()))
			return "";
		else
			return saveMethod;
	}

	public final SessionGlobal getSessionGlobal() {
		return sessionGbl;
	}

	private String getSource() {
		if (getAnnotation() != null)
			return getAnnotation().regName();
		else
			return request.getParameter("Source");
	}

	public Object getSubFunctionKey(PageStyle style, String operation) {
		return moduleProvider == null ? null : moduleProvider
				.getSubFunctionKey(style, operation);
	}

	public boolean getSupportDoubleTransform(PageStyle style, String operation) {
		return moduleProvider == null ? true : moduleProvider
				.getSupportDoubleTransform(style, operation);
	}

	protected String getTabSheetCondition(String tab) {
		return moduleProvider == null ? "" : moduleProvider
				.getTabSheetCondition(tab);
	}

	public Node getXmlDocument(int i, PageStyle style, String operation) {
		return dataXmlDocument;
	}

	public String getXsltFile(boolean isIe, PageStyle style, String operation) {
		return moduleProvider == null ? null : moduleProvider.getXsltFile(isIe,
				style, operation);
	}

	protected abstract void handleInformationException(
			HttpServletRequest request, DataSet postDataSet, PageStyle style);

	protected final void handleInformationException(HttpServletRequest request,
			DataSet postDataSet, PageStyle style,
			AbstractXmlTableResolver... resolvers) {
		BaseDataSet getDataSet = new BaseDataSet();
		this.setConnectionString(getDataSet);
		for (AbstractXmlTableResolver resolver : resolvers)
			resolver.internalSetHostDataSet(getDataSet);

		boolean oldInput = isQueryStringInput();
		setQueryStringInput(false);

		DataTable[] tempTables = new DataTable[resolvers.length];
		try {
			setData(false, style, "", request);
			for (int i = 0; i < resolvers.length; ++i) {
				AbstractXmlTableResolver resolver = resolvers[i];
				DataTable table = resolver.getHostTable();
				if (table == null)
					table = resolver.selectTableStructure();
				tempTables[i] = table;
			}
		} finally {
			setQueryStringInput(oldInput);
			for (AbstractXmlTableResolver resolver : resolvers)
				resolver.setHostDataSet(this);
		}
		for (int i = 0; i < resolvers.length; ++i) {
			if (getTables().contains(resolvers[i].getTableName()))
				getTables().remove(resolvers[i].getTableName());
			DataTable dstTable = DataSetUtil.createStringTable(tempTables[i]);
			getTables().add(dstTable);
		}

		DataTable errorTable = null;
		for (int i = 0; i < resolvers.length; ++i) {
			AbstractXmlTableResolver resolver = resolvers[i];
			resolver.mergeErrorTable(getTables().getItem(
					resolver.getTableName()), postDataSet.getTables().getItem(
					resolver.getTableName()), tempTables[i]);
			if (i == 0)
				errorTable = resolver.getErrorObjects().getErrorTable();
			else
				DataSetCopyUtil.appendDataTable(resolver.getErrorObjects()
						.getErrorTable(), errorTable);
		}

		for (DataTable table : getDataSet.getTables()) {
			if (!this.getTables().contains(table.getTableName()))
				this.getTables().add(table.copy());
		}
		getTables().add(errorTable);
	}

	protected final void handleInformationException(HttpServletRequest request,
			DataSet postDataSet, PageStyle style,
			AbstractXmlTableResolver resolver,
			TableAdapterCollection<AbstractXmlTableResolver> resolverCollection) {
		AbstractXmlTableResolver[] resolvers = new AbstractXmlTableResolver[resolverCollection
				.size() + 1];
		resolvers[0] = resolver;
		for (int i = 0; i < resolverCollection.size(); ++i)
			resolvers[i + 1] = resolverCollection.get(i);

		handleInformationException(request, postDataSet, style, resolvers);
	}

	protected final void handleInformationException(
			HttpServletRequest request,
			DataSet postDataSet,
			PageStyle style,
			TableAdapterCollection<AbstractXmlTableResolver> resolvers,
			TableAdapterCollection<AbstractXmlTableResolver>... resolverCollections) {
		handleInformationException(request, postDataSet, style, resolvers
				.toArray(resolverCollections));
	}

	protected void internalSetDataXmlDocument(PageStyle style,
			String operation, boolean isPost, Node data) {
	}

	public final boolean isQueryStringInput() {
		return queryStringInput;
	}

	public final boolean isSortQuery() {
		return sortQuery;
	}

	public final boolean isSupportData() {
		return supportData;
	}

	protected void navigateData(PageStyle style) {
	}

	protected void onFilledCustomTables(FilledCustomEventArgs e) {
		if (filledCustomListener != null)
			filledCustomListener.filledCustomTables(e);
	}

	protected void onFilledListTables(FilledListEventArgs e) {
		if (filledListListener != null)
			filledListListener.filledListTables(e);
	}

	protected void onFilledDetailListTables(FilledDetailListEventArgs e) {
		if (filledDetailListListener != null)
			filledDetailListListener.filledDetailListTables(e);
	}

	protected void onFillingDetailListTables(FillingDetailListEventArgs e) {
		if (fillingDetailListListener != null)
			fillingDetailListListener.fillingDetailListTables(e);
	}

	protected void onFilledInsertTables(FilledInsertEventArgs e) {
		if (filledInsertListener != null)
			filledInsertListener.filledInsertTables(e);
	}

	protected void onFilledUpdateTables(FilledUpdateEventArgs e) {
		if (filledUpdateListener != null)
			filledUpdateListener.filledUpdateTables(e);
	}

	protected void onFillingUpdateTables(FillingUpdateEventArgs e) {
		if (fillingUpdateListener != null)
			fillingUpdateListener.fillingUpdateTables(e);
	}

	public String post(PageStyle style, String operation,
			HttpServletRequest request, DataSet postDataSet) {
		setPostData(style, operation, request, postDataSet);

		return "";
	}

	protected final void postData(PageStyle style, HttpServletRequest request,
			DataSet postDataSet, AbstractXmlTableResolver... resolvers) {
		switch (style) {
		case Insert:
			preparePostDataSet(postDataSet, UpdateKind.Insert);
			for (AbstractXmlTableResolver resolver : resolvers)
				resolver.checkFirstConstraints(UpdateKind.Insert, postDataSet);
			for (AbstractXmlTableResolver resolver : resolvers)
				resolver.insert(postDataSet);
			break;
		case Update:
			preparePostDataSet(postDataSet, UpdateKind.Update);
			for (AbstractXmlTableResolver resolver : resolvers)
				resolver.checkFirstConstraints(UpdateKind.Update, postDataSet);
			for (AbstractXmlTableResolver resolver : resolvers)
				resolver.update(postDataSet);
			break;
		}
		try {
			boolean error = false;
			for (AbstractXmlTableResolver resolver : resolvers)
				try {
					resolver.checkConstraints(postDataSet);
					resolver.checkError();
				} catch (InformationException ex) {
					error = true;
				}
			if (error)
				throw new InformationException();
		} catch (InformationException ex) {
			handleInformationException(request, postDataSet, style);
			throw ex;
		}
		commitData();
	}

	protected final void postData(PageStyle style, HttpServletRequest request,
			DataSet postDataSet,
			TableAdapterCollection<AbstractXmlTableResolver> resolvers) {
		AbstractXmlTableResolver[] data = new AbstractXmlTableResolver[resolvers
				.size()];
		postData(style, request, postDataSet, resolvers.toArray(data));
	}

	protected final void postData(
			PageStyle style,
			HttpServletRequest request,
			DataSet postDataSet,
			TableAdapterCollection<AbstractXmlTableResolver> resolvers,
			TableAdapterCollection<AbstractXmlTableResolver>... resolverCollections) {
		postData(style, request, postDataSet, resolvers
				.toArray(resolverCollections));
	}

	protected void preparePostDataSet(DataSet postDataSet, UpdateKind kind) {
	}

	protected abstract void processCodeTables(PageStyle style);

	public final void setData(boolean isPost, PageStyle style,
			String operation, HttpServletRequest request) {
		setData(isPost, style, operation, request, null);
	}

	private void setData(boolean isPost, PageStyle style, String operation,
			HttpServletRequest request, DataSet postDataSet) {
		switch (style) {
		case Custom:
			setDataXmlDocument(style, operation, isPost);
			fillCustomTables(isPost, operation, request, postDataSet);
			onFilledCustomTables(FilledCustomEventArgs.getArgs(this, isPost,
					operation, request, postDataSet));
			break;
		case Insert:
			setDataXmlDocument(style, operation, isPost);
			setInsertData(isPost, request, postDataSet);
			break;
		case Update:
			setDataXmlDocument(style, operation, isPost);
			if (!isPost)
				setUpdateData(false, request, null);
			else
				setUpdateData(true, request, postDataSet);
			break;
		case Detail:
			setDataXmlDocument(style, operation, isPost);
			setDetailData(request);
			break;
		case List:
			setDataXmlDocument(style, operation, isPost);
			if (!isPost)
				setListData(false, request, null);
			else
				setListData(true, request, postDataSet);
			break;
		case Delete:
			setDeleteData(request);
			break;
		}
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

	public final void setDefaultOrder(String defaultOrder) {
		this.defaultOrder = defaultOrder;
	}

	public final void setDefaultSortField(int defaultSortField) {
		this.defaultSortField = defaultSortField;
	}

	protected final <T extends AbstractXmlDataAdapter> void setDefaultValue(
			T... resolvers) {
		for (AbstractXmlDataAdapter resolver : resolvers) {
			for (DataRow row : resolver.getHostTable().getRows())
				resolver.setDefaultValue(row);
		}
	}

	private void setDeleteData(HttpServletRequest request) {
		if (disablePage != null && disablePage.contains(DisableFunction.Delete))
			throw new DisableFuncException(getSource(), PageStyle.Delete
					.ordinal());

		String key = request.getParameter("ID");
		fillingUpdateEvent(false, key, PageStyle.Delete, request, null);
		fillUpdateTables(key, PageStyle.Delete, request);
		onFilledUpdateTables(FilledUpdateEventArgs.getArgs(this, false, key,
				PageStyle.Delete, request, null));

		if (supportData)
			checkDelete(new DataRightEventArgs(this));

		delete();
	}

	private void setDetailData(HttpServletRequest request) {
		if (disablePage != null && disablePage.contains(DisableFunction.Detail))
			throw new DisableFuncException(getSource(), PageStyle.Detail
					.ordinal());

		String key = request.getParameter("ID");
		fillingUpdateEvent(false, key, PageStyle.Detail, request, null);
		fillUpdateTables(key, PageStyle.Detail, request);
		onFilledUpdateTables(FilledUpdateEventArgs.getArgs(this, false, key,
				PageStyle.Detail, request, null));

		if (supportData)
			checkPublic(new DataRightEventArgs(this));

		fillingDetailListEvent();
		fillDetailListTables();
		onFilledDetailListTables(FilledDetailListEventArgs.getArgs(this));

		navigateData(PageStyle.Detail);
		processCodeTables(PageStyle.Detail);
	}

	public final void setDisablePage(EnumSet<DisableFunction> disablePage) {
		this.disablePage = disablePage;
	}

	public final void setFilterSQL(MarcoConfigItem filterSQL) {
		this.filterSQL = filterSQL;
	}

	public final void setFunctionKey(Object functionKey) {
		this.functionKey = functionKey;
	}

	public final void setFuncType(FunctionRightType funcType) {
		this.funcType = funcType;
	}

	private void setInsertData(boolean isPost, HttpServletRequest request,
			DataSet postDataSet) {
		if (disablePage != null && disablePage.contains(DisableFunction.Insert))
			throw new DisableFuncException(getSource(), PageStyle.Insert
					.ordinal());

		if (supportData)
			checkReadOnly(new DataRightEventArgs(this));

		fillInsertTables(isPost, request, postDataSet);

		onFilledInsertTables(FilledInsertEventArgs.getArgs(this, isPost,
				request, postDataSet));

		if (!isPost) {
			navigateData(PageStyle.Insert);
			processCodeTables(PageStyle.Insert);
		}
	}

	protected void setListCondition(QueryParamCondition condition) {
	}

	private void setListData(boolean isPost, HttpServletRequest request,
			DataSet postDataSet) {
		if (disablePage != null && disablePage.contains(DisableFunction.List))
			throw new DisableFuncException(getSource(), PageStyle.List
					.ordinal());

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

	public final void setModuleProvider(IModuleProvider moduleProvider) {
		this.moduleProvider = moduleProvider;
	}

	public final void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	protected final void setPostData(PageStyle style, String operation,
			HttpServletRequest request, DataSet postDataSet) {
		saveMethod = postDataSet.getTables().getItem("OtherInfo").getRows()
				.getItem(0).getItem("Save").toString().toLowerCase();
		setData(true, style, operation, request, postDataSet);
	}

	public final void setQueryStringInput(boolean queryStringInput) {
		this.queryStringInput = queryStringInput;
	}

	protected void setResolvers() {

	}

	protected void setRightProperties(AbstractXmlTableResolver resolver,
			DataRightEventArgs e) {
		e.setProperty(resolver.getTableName());
		resolver.setRightArgs(e);
	}

	public final void setSortQuery(boolean sortQuery) {
		this.sortQuery = sortQuery;
	}

	public final void setSupportData(boolean supportData) {
		this.supportData = supportData;
	}

	protected final void setTableResolverData(AbstractXmlTableResolver resolver) {
		if (resolver != null)
			resolver.setData(request, response, appGbl, sessionGbl);
	}

	private void setUpdateData(boolean isPost, HttpServletRequest request,
			DataSet postDataSet) {
		if (disablePage != null && disablePage.contains(DisableFunction.Update))
			throw new DisableFuncException(getSource(), PageStyle.Update
					.ordinal());

		if (!isPost) {
			String key = request.getParameter("ID");
			fillingUpdateEvent(false, key, PageStyle.Update, request, null);
			fillUpdateTables(key, PageStyle.Update, request);
			onFilledUpdateTables(FilledUpdateEventArgs.getArgs(this, false,
					key, PageStyle.Update, request, null));
		} else {
			fillingUpdateEvent(true, null, PageStyle.Update, request,
					postDataSet);
			fillUpdateTables(request, postDataSet);
			onFilledUpdateTables(FilledUpdateEventArgs.getArgs(this, true,
					null, PageStyle.Update, request, postDataSet));
		}

		if (supportData)
			checkReadWrite(new DataRightEventArgs(this));

		if (!isPost) {
			navigateData(PageStyle.Update);
			processCodeTables(PageStyle.Update);
		}
	}

	public final void addAdapter(
			AdapterAddedEventArgs<AbstractXmlTableResolver> e) {
		setTableResolverData(e.getAdapter());
	}

	public final synchronized void addCommittedDataListener(
			CommittedDataListener l) {
		committedDataListener = CommittedDataEventMulticaster.add(
				committedDataListener, l);
	}

	public final synchronized void addCommittingDataListener(
			CommittingDataListener l) {
		committingDataListener = CommittingDataEventMulticaster.add(
				committingDataListener, l);
	}

	public final synchronized void addFilledInsertListener(
			FilledInsertListener l) {
		filledInsertListener = FilledInsertEventMulticaster.add(
				filledInsertListener, l);
	}

	public final synchronized void removeCommittedDataListener(
			CommittedDataListener l) {
		committedDataListener = CommittedDataEventMulticaster.remove(
				committedDataListener, l);
	}

	public final synchronized void removeCommittingDataListener(
			CommittingDataListener l) {
		committingDataListener = CommittingDataEventMulticaster.remove(
				committingDataListener, l);
	}

	public final synchronized void removeFilledInsertListener(
			FilledInsertListener l) {
		filledInsertListener = FilledInsertEventMulticaster.remove(
				filledInsertListener, l);
	}

	public final synchronized void addFilledDetailListListener(
			FilledDetailListListener l) {
		filledDetailListListener = FilledDetailListEventMulticaster.add(
				filledDetailListListener, l);
	}

	public final synchronized void addFilledUpdateListener(
			FilledUpdateListener l) {
		filledUpdateListener = FilledUpdateEventMulticaster.add(
				filledUpdateListener, l);
	}

	public final synchronized void addFillingDetailListListener(
			FillingDetailListListener l) {
		fillingDetailListListener = FillingDetailListEventMulticaster.add(
				fillingDetailListListener, l);
	}

	public final synchronized void addFillingUpdateListener(
			FillingUpdateListener l) {
		fillingUpdateListener = FillingUpdateEventMulticaster.add(
				fillingUpdateListener, l);
	}

	public final synchronized void removeFilledDetailListListener(
			FilledDetailListListener l) {
		filledDetailListListener = FilledDetailListEventMulticaster.remove(
				filledDetailListListener, l);
	}

	public final synchronized void removeFilledUpdateListener(
			FilledUpdateListener l) {
		filledUpdateListener = FilledUpdateEventMulticaster.remove(
				filledUpdateListener, l);
	}

	public final synchronized void removeFillingDetailListListener(
			FillingDetailListListener l) {
		fillingDetailListListener = FillingDetailListEventMulticaster.remove(
				fillingDetailListListener, l);
	}

	public final synchronized void removeFillingUpdateListener(
			FillingUpdateListener l) {
		fillingUpdateListener = FillingUpdateEventMulticaster.remove(
				fillingUpdateListener, l);
	}

	public final synchronized void addFilledCustomListener(
			FilledCustomListener l) {
		filledCustomListener = FilledCustomEventMulticaster.add(
				filledCustomListener, l);
	}

	public final synchronized void addFilledListListener(FilledListListener l) {
		filledListListener = FilledListEventMulticaster.add(filledListListener,
				l);
	}

	public final synchronized void removeFilledCustomListener(
			FilledCustomListener l) {
		filledCustomListener = FilledCustomEventMulticaster.remove(
				filledCustomListener, l);
	}

	public final synchronized void removeFilledListListener(FilledListListener l) {
		filledListListener = FilledListEventMulticaster.remove(
				filledListListener, l);
	}
}
