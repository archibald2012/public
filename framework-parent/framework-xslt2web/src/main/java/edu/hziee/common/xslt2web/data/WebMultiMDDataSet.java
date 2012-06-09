package edu.hziee.common.xslt2web.data;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.exception.NoRecordException;
import edu.hziee.common.xslt2web.right.DataRightEventArgs;
import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sysutil.Resource;

public abstract class WebMultiMDDataSet extends WebUpdateDataSet implements
		INonUIResolvers, IMainTableResolver {
	private AbstractXmlTableResolver mainResolver;
	private TableAdapterCollection<AbstractXmlTableResolver> masterResolvers;
	private TableAdapterCollection<AbstractXmlTableResolver> detailResolvers;
	private AbstractXmlTableResolver listViewResolver;
	private TableAdapterCollection<AbstractXmlTableResolver> nonUIResolvers;
	private TableRelationCollection masterRelations;
	private TableRelationCollection detailRelations;

	private boolean cascadeUpdates;
	private boolean cascadeDeletes;

	public WebMultiMDDataSet() {
		cascadeDeletes = cascadeUpdates = true;

		masterResolvers = new TableAdapterCollection<AbstractXmlTableResolver>(
				AbstractXmlTableResolver.class);
		masterResolvers.addAdapterAddedListener(this);
		detailResolvers = new TableAdapterCollection<AbstractXmlTableResolver>(
				AbstractXmlTableResolver.class);
		detailResolvers
				.addAdapterAddedListener(new AdapterAddedListener<AbstractXmlTableResolver>() {
					public void addAdapter(
							AdapterAddedEventArgs<AbstractXmlTableResolver> e) {
						setTableResolverData(e.getAdapter());
						e.getAdapter().setUpdateMode(UpdateMode.Merge);
					}
				});

		detailRelations = new TableRelationCollection();
		masterRelations = new TableRelationCollection();
	}

	public final void setListView(AbstractXmlTableResolver listViewResolver) {
		this.listViewResolver = listViewResolver;
	}

	public final boolean isCascadeUpdates() {
		return cascadeUpdates;
	}

	public final void setCascadeUpdates(boolean cascadeUpdates) {
		this.cascadeUpdates = cascadeUpdates;
	}

	public final boolean isCascadeDeletes() {
		return cascadeDeletes;
	}

	public final void setCascadeDeletes(boolean cascadeDeletes) {
		this.cascadeDeletes = cascadeDeletes;
	}

	public final TableAdapterCollection<AbstractXmlTableResolver> getMasterResolvers() {
		return masterResolvers;
	}

	public final TableAdapterCollection<AbstractXmlTableResolver> getDetailResolvers() {
		return detailResolvers;
	}

	public final TableRelationCollection getMasterRelations() {
		return masterRelations;
	}

	public final TableRelationCollection getDetailRelations() {
		return detailRelations;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void commitData() {
		committingData();
		updateTableresolvers(masterResolvers, detailResolvers, nonUIResolvers);
		commitData();
	}

	@Override
	protected void delete() {
		for (AbstractXmlTableResolver resolver : masterResolvers)
			resolver.delete();
		if (cascadeDeletes)
			for (AbstractXmlTableResolver resolver : detailResolvers)
				resolver.delete();
		commitData();
	}

	@Override
	protected void fillInsertTables(boolean isPost, HttpServletRequest request,
			DataSet postDataSet) {
		fillInsertTables(isPost, masterResolvers);
		fillInsertTables(isPost, detailResolvers);

		if (!isPost && isQueryStringInput())
			ResolverUtil.setDefaultValues(getMainResolver(), request);
		if (!isPost) {
			setDefaultValue(masterResolvers.toArray());
			setDefaultValue(detailResolvers.toArray());
		}
	}

	protected void addFillingUpdateTables(FillingUpdateEventArgs e) {
		e.getHandled().addResolvers(getMainResolver());
		masterRelations.addFillingResolver(e.getHandled());
		detailRelations.addFillingResolver(e.getHandled());
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
		masterRelations.fillDetailTable(getFillingUpdateArgs().getHandled());
		detailRelations.fillDetailTable(getFillingUpdateArgs().getHandled());
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
		masterRelations.fillDetailTable(getFillingUpdateArgs().getHandled());
		detailRelations.fillDetailTable(getFillingUpdateArgs().getHandled());

		for (AbstractXmlTableResolver resolver : masterResolvers) {
			if (style == PageStyle.Update
					&& !getFillingUpdateArgs().getHandled().getItem(resolver))
				ResolverUtil.getConstraints(UpdateKind.Update, resolver);
		}
		for (AbstractXmlTableResolver resolver : detailResolvers) {
			if (style == PageStyle.Update
					&& !getFillingUpdateArgs().getHandled().getItem(resolver))
				ResolverUtil.getConstraints(UpdateKind.Update, resolver);
		}
	}

	@Override
	protected void preparePostDataSet(DataSet postDataSet, UpdateKind kind) {
		for (AbstractXmlTableResolver resolver : detailResolvers)
			resolver.prepareDataSet(postDataSet);
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
		for (AbstractXmlTableResolver resolver : masterResolvers)
			builder.append(resolver.getConstraints().getJavaScript());
		for (AbstractXmlTableResolver resolver : detailResolvers)
			builder.append(resolver.getConstraints().getJavaScript());
		return String.format(Resource.JScriptSkeleton, "", builder.toString());
	}

	@Override
	public AbstractXmlTableResolver getListView() {
		return listViewResolver;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void handleInformationException(HttpServletRequest request,
			DataSet postDataSet, PageStyle style) {
		handleInformationException(request, postDataSet, style,
				masterResolvers, detailResolvers);
	}

	@Override
	protected void internalSetDataXmlDocument(PageStyle style,
			String operation, boolean isPost, Node data) {
		switch (style) {
		case Insert:
		case Update:
		case Detail:
		case Delete:
			for (AbstractXmlTableResolver resolver : masterResolvers)
				resolver.setDocument(style, isPost, data);
			for (AbstractXmlTableResolver resolver : detailResolvers)
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
			for (AbstractXmlTableResolver resolver : masterResolvers)
				ResolverUtil.processRegCodeTables(resolver, getCodeTables(),
						getCommand(), style);
			for (AbstractXmlTableResolver resolver : detailResolvers)
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
			for (AbstractXmlTableResolver resolver : masterResolvers)
				resolver.navigateData(style);
			for (AbstractXmlTableResolver resolver : detailResolvers)
				resolver.navigateData(style);
			break;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String post(PageStyle style, String operation,
			HttpServletRequest request, DataSet postDataSet) {
		super.post(style, operation, request, postDataSet);
		switch (style) {
		case Insert:
		case Update:
			postData(style, request, postDataSet, getMasterResolvers(),
					getDetailResolvers());
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
		return this.mainResolver;
	}

	public final void setMainResolver(AbstractXmlTableResolver mainResolver) {
		this.mainResolver = mainResolver;
		if (listViewResolver == null)
			listViewResolver = mainResolver;
	}

}
