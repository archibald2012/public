package edu.hziee.common.xslt2web.data;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.exception.NoRecordException;
import edu.hziee.common.xslt2web.right.DataRightEventArgs;
import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sysutil.Resource;

public abstract class WebMDDataSet extends WebUpdateDataSet implements
		IMainTableResolver, INonUIResolvers {
	private AbstractXmlTableResolver masterResolver;
	private AbstractXmlTableResolver detailResolver;
	private AbstractXmlTableResolver listViewResolver;
	private TableAdapterCollection<AbstractXmlTableResolver> nonUIResolvers;
	private TableRelation relation;

	private boolean cascadeUpdates;
	private boolean cascadeDeletes;

	public WebMDDataSet() {
		cascadeDeletes = cascadeUpdates = true;
	}

	protected void addFillingUpdateTables(FillingUpdateEventArgs e) {
		e.getHandled().addResolvers(getMasterResolver());
		relation.addFillingResolver(e.getHandled());
	}

	@Override
	protected void checkDelete(DataRightEventArgs e) {
		setRightProperties(getMasterResolver(), e);
		if (!e.isChecked())
			getMasterResolver().checkDelete(getDataRight(), e.getRightType(),
					e.getOwnerField().toString());
	}

	@Override
	protected void checkPublic(DataRightEventArgs e) {
		setRightProperties(getMasterResolver(), e);
		if (!e.isChecked())
			getMasterResolver().checkPublic(getDataRight(), e.getRightType(),
					e.getOwnerField().toString());
	}

	@Override
	protected void checkReadOnly(DataRightEventArgs e) {
		setRightProperties(getMasterResolver(), e);
		if (!e.isChecked())
			getDataRight().checkReadOnly(e.getRightType(),
					getInfo().getUserID());
	}

	@Override
	protected void checkReadWrite(DataRightEventArgs e) {
		setRightProperties(getMasterResolver(), e);
		if (!e.isChecked())
			getMasterResolver().checkReadWrite(getDataRight(),
					e.getRightType(), e.getOwnerField().toString());
	}

	@Override
	protected void commitData() {
		committingData();
		updateTableresolvers(new AbstractXmlTableResolver[] { masterResolver,
				detailResolver }, nonUIResolvers);
		committedData();
	}

	@Override
	protected void delete() {
		masterResolver.delete();
		if (cascadeDeletes)
			detailResolver.delete();
		commitData();
	}

	@Override
	protected void fillInsertTables(boolean isPost, HttpServletRequest request,
			DataSet postDataSet) {
		fillInsertTables(isPost, getMasterResolver(), getDetailResolver());
		if (!isPost && isQueryStringInput())
			ResolverUtil.setDefaultValues(getMasterResolver(), request);
		if (!isPost)
			setDefaultValue(getMasterResolver(), getDetailResolver());
	}

	@Override
	protected void fillUpdateTables(HttpServletRequest request,
			DataSet postDataSet) {
		AbstractXmlTableResolver master = getMasterResolver();

		if (!getFillingUpdateArgs().getHandled().getItem(master)) {
			ResolverUtil.setOldKeyValues(master, postDataSet);
			if (master.getHostTable().getRows().size() == 0)
				throw new NoRecordException();
		}
		getRelation().fillDetailTable(getFillingUpdateArgs().getHandled());
		// getRelation().fillDetailTable();
	}

	@Override
	protected void fillUpdateTables(String key, PageStyle style,
			HttpServletRequest request) {
		AbstractXmlTableResolver master = getMasterResolver();
		AbstractXmlTableResolver detail = getDetailResolver();

		if (!getFillingUpdateArgs().getHandled().getItem(master)) {
			ResolverUtil.setKeyValues(master, request, key);
			if (style != PageStyle.Delete)
				master.addVirtualFields();
		}
		if (!getFillingUpdateArgs().getHandled().getItem(detail)) {
			getRelation().fillDetailTable(getFillingUpdateArgs().getHandled());
			//getRelation().fillDetailTable();
			if (style != PageStyle.Delete)
				detail.addVirtualFields();
		}
		if (style == PageStyle.Update)
			ResolverUtil.getConstraints(UpdateKind.Update, master, detail);
	}

	public final AbstractXmlTableResolver getDetailResolver() {
		// Debug.Assert(fDetailResolver != null,
		// "靠，你DetailResolver都不赋值，就想用，见鬼去吧。");
		return detailResolver;
	}

	@Override
	public String getJScript(PageStyle style, String operation) {
		String constraintScript = getMasterResolver().getConstraints()
				.getJavaScript()
				+ getDetailResolver().getConstraints().getJavaScript();
		return String.format(Resource.JScriptSkeleton, "", constraintScript);
	}

	@Override
	public AbstractXmlTableResolver getListView() {
		return listViewResolver;
	}

	public AbstractXmlTableResolver getMainResolver() {
		return getMasterResolver();
	}

	public final AbstractXmlTableResolver getMasterResolver() {
		// Debug.Assert(fMasterResolver != null,
		// "靠，你MasterResolver都不赋值，就想用，见鬼去吧。");
		return masterResolver;
	}

	public TableAdapterCollection<AbstractXmlTableResolver> getNonUIResolvers() {
		if (nonUIResolvers == null) {
			nonUIResolvers = new TableAdapterCollection<AbstractXmlTableResolver>(AbstractXmlTableResolver.class);
			nonUIResolvers.addAdapterAddedListener(this);
		}
		return nonUIResolvers;
	}

	public final TableRelation getRelation() {
		return relation;
	}

	@Override
	protected void handleInformationException(HttpServletRequest request,
			DataSet postDataSet, PageStyle style) {
		handleInformationException(request, postDataSet, style,
				getMasterResolver(), getDetailResolver());
	}

	@Override
	protected void internalSetDataXmlDocument(PageStyle style,
			String operation, boolean isPost, Node data) {
		switch (style) {
		case Insert:
		case Update:
		case Detail:
		case Delete:
			getMasterResolver().setDocument(style, isPost, data);
			getDetailResolver().setDocument(style, isPost, data);
			break;
		case List:
			getListView().setDocument(style, isPost, data);
			break;
		}
	}

	public final boolean isCascadeDeletes() {
		return cascadeDeletes;
	}

	public final boolean isCascadeUpdates() {
		return cascadeUpdates;
	}

	@Override
	protected void navigateData(PageStyle style) {
		getMasterResolver().navigateData(style);
		getDetailResolver().navigateData(style);
	}

	@Override
	public String post(PageStyle style, String operation,
			HttpServletRequest request, DataSet postDataSet) {
		super.post(style, operation, request, postDataSet);

		switch (style) {
		case Insert:
		case Update:
			postData(style, request, postDataSet, getMasterResolver(),
					getDetailResolver());
			break;
		}
		return "";
	}

	@Override
	protected void preparePostDataSet(DataSet postDataSet, UpdateKind kind) {
		getDetailResolver().prepareDataSet(postDataSet);
	}

	@Override
	protected void processCodeTables(PageStyle style) {
		ResolverUtil.processRegCodeTables(getMasterResolver(), getCodeTables(),
				getCommand(), style);

		if (style != PageStyle.List)
			ResolverUtil.processRegCodeTables(getDetailResolver(),
					getCodeTables(), getCommand(), style);
	}

	public final void setCascadeDeletes(boolean cascadeDeletes) {
		this.cascadeDeletes = cascadeDeletes;
	}

	public final void setCascadeUpdates(boolean cascadeUpdates) {
		this.cascadeUpdates = cascadeUpdates;
	}

	public final void setDetailResolver(AbstractXmlTableResolver detailResolver) {
		this.detailResolver = detailResolver;
		detailResolver.setUpdateMode(UpdateMode.Merge);
		setTableResolverData(detailResolver);
	}

	public final void setMasterResolver(AbstractXmlTableResolver masterResolver) {
		this.masterResolver = masterResolver;
		if (listViewResolver == null)
			listViewResolver = masterResolver;
		setTableResolverData(masterResolver);
	}

	public final void setRelation(TableRelation relation) {
		this.relation = relation;
	}
}
