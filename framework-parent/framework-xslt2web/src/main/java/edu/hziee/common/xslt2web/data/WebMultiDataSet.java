package edu.hziee.common.xslt2web.data;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.exception.NoRecordException;
import edu.hziee.common.xslt2web.right.DataRightEventArgs;
import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sysutil.Resource;

public abstract class WebMultiDataSet extends WebUpdateDataSet implements
		INonUIResolvers, IMainTableResolver {
	private TableAdapterCollection<AbstractXmlTableResolver> uiResolvers;
	private TableAdapterCollection<AbstractXmlTableResolver> nonUIResolvers;
	private TableRelationCollection relations;
	private AbstractXmlTableResolver listViewResolver;
	private AbstractXmlTableResolver mainResolver;

	public WebMultiDataSet() {
		uiResolvers = new TableAdapterCollection<AbstractXmlTableResolver>(
				AbstractXmlTableResolver.class);
		uiResolvers.addAdapterAddedListener(this);
		relations = new TableRelationCollection();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void commitData() {
		committingData();
		updateTableresolvers(uiResolvers, nonUIResolvers);
		committedData();
	}

	@Override
	protected void delete() {
		for (AbstractXmlTableResolver resolver : uiResolvers)
			resolver.delete();
		commitData();
	}

	@Override
	protected void fillInsertTables(boolean isPost, HttpServletRequest request,
			DataSet postDataSet) {
		fillInsertTables(isPost, uiResolvers);

		if (!isPost && isQueryStringInput())
			for (AbstractXmlTableResolver resolver : uiResolvers)
				ResolverUtil.setDefaultValues(resolver, request);
		if (!isPost) {
			setDefaultValue(uiResolvers.toArray());
		}
	}

	protected void addFillingUpdateTables(FillingUpdateEventArgs e) {
		e.getHandled().addResolvers(getMainResolver());
		relations.addFillingResolver(e.getHandled());
	}

	@Override
	protected void fillUpdateTables(HttpServletRequest request,
			DataSet postDataSet) {
		AbstractXmlTableResolver master = getMainResolver();

		if (!getFillingUpdateArgs().getHandled().getItem(master)) {
			ResolverUtil.setOldKeyValues(master, postDataSet);
			if (master.getHostTable().getRows().size() == 0)
				throw new NoRecordException();
		}
		relations.fillDetailTable(getFillingUpdateArgs().getHandled());
	}

	@Override
	protected void fillUpdateTables(String key, PageStyle style,
			HttpServletRequest request) {
		AbstractXmlTableResolver master = getMainResolver();

		if (!getFillingUpdateArgs().getHandled().getItem(master)) {
			ResolverUtil.setKeyValues(master, request, key);
			if (style != PageStyle.Delete)
				master.addVirtualFields();
		}
		relations.fillDetailTable(getFillingUpdateArgs().getHandled());

		for (AbstractXmlTableResolver resolver : uiResolvers) {
			if (style == PageStyle.Update
					&& !getFillingUpdateArgs().getHandled().getItem(resolver))
				ResolverUtil.getConstraints(UpdateKind.Update, resolver);
		}
	}

	@Override
	protected void checkDelete(DataRightEventArgs e) {
		setRightProperties(getMainResolver(), e);
		if (!e.isChecked())
			getMainResolver().checkDelete(getDataRight(), e.getRightType(),
					e.getOwnerField().toString());
	}

	@Override
	protected void checkPublic(DataRightEventArgs e) {
		setRightProperties(getMainResolver(), e);
		if (!e.isChecked())
			getMainResolver().checkPublic(getDataRight(), e.getRightType(),
					e.getOwnerField().toString());
	}

	@Override
	protected void checkReadOnly(DataRightEventArgs e) {
		setRightProperties(getMainResolver(), e);
		if (!e.isChecked())
			getDataRight().checkReadOnly(e.getRightType(),
					getInfo().getUserID());
	}

	@Override
	protected void checkReadWrite(DataRightEventArgs e) {
		setRightProperties(getMainResolver(), e);
		if (!e.isChecked())
			getMainResolver().checkReadWrite(getDataRight(), e.getRightType(),
					e.getOwnerField().toString());
	}

	@Override
	public String getJScript(PageStyle style, String operation) {
		StringBuilder builder = new StringBuilder();
		for (AbstractXmlTableResolver resolver : uiResolvers)
			builder.append(resolver.getConstraints().getJavaScript());
		return String.format(Resource.JScriptSkeleton, "", builder.toString());
	}

	@Override
	public final AbstractXmlTableResolver getListView() {
		return listViewResolver;
	}

	@Override
	protected void handleInformationException(HttpServletRequest request,
			DataSet postDataSet, PageStyle style) {
		handleInformationException(request, postDataSet, style, uiResolvers);
	}

	protected void internalSetDataXmlDocument(PageStyle style,
			String operation, boolean isPost, Node data) {
		switch (style) {
		case Insert:
		case Update:
		case Detail:
		case Delete:
			for (AbstractXmlTableResolver resolver : uiResolvers)
				resolver.setDocument(style, isPost, data);
			break;
		case List:
			getListView().setDocument(style, isPost, data);
			break;
		}
	}

	@Override
	protected void processCodeTables(PageStyle style) {
		switch (style) {
		case List:
			ResolverUtil.processRegCodeTables(getListView(), getCodeTables(),
					getCommand(), style);
			break;
		default:
			for (AbstractXmlTableResolver resolver : uiResolvers)
				ResolverUtil.processRegCodeTables(resolver, getCodeTables(),
						getCommand(), style);
			break;
		}
	}

	@Override
	protected void navigateData(PageStyle style) {
		switch (style) {
		case List:
			getListView().navigateData(style);
			break;
		default:
			for (AbstractXmlTableResolver resolver : uiResolvers)
				resolver.navigateData(style);
			break;
		}
	}

	@Override
	public String post(PageStyle style, String operation,
			HttpServletRequest request, DataSet postDataSet) {
		super.post(style, operation, request, postDataSet);
		switch (style) {
		case Insert:
		case Update:
			postData(style, request, postDataSet, uiResolvers);
			break;
		}
		return "";
	}

	public final TableAdapterCollection<AbstractXmlTableResolver> getNonUIResolvers() {
		if (nonUIResolvers == null) {
			nonUIResolvers = new TableAdapterCollection<AbstractXmlTableResolver>(
					AbstractXmlTableResolver.class);
			nonUIResolvers.addAdapterAddedListener(this);
		}
		return nonUIResolvers;
	}

	public final AbstractXmlTableResolver getMainResolver() {
		return mainResolver;
	}

	public final void setMainResolver(AbstractXmlTableResolver mainResolver) {
		this.mainResolver = mainResolver;
		if (listViewResolver == null)
			listViewResolver = mainResolver;
	}

	public final TableAdapterCollection<AbstractXmlTableResolver> getUiResolvers() {
		return uiResolvers;
	}

	public final TableRelationCollection getRelations() {
		return relations;
	}

	public final void setListView(AbstractXmlTableResolver listview) {
		this.listViewResolver = listview;
	}
}
