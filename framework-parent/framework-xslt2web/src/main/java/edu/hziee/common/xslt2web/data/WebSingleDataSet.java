package edu.hziee.common.xslt2web.data;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.exception.NoRecordException;
import edu.hziee.common.xslt2web.right.DataRightEventArgs;
import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sysutil.Resource;

public abstract class WebSingleDataSet extends WebUpdateDataSet implements
		IMainTableResolver, INonUIResolvers {
	private AbstractXmlTableResolver mainResolver;
	private TableAdapterCollection<AbstractXmlTableResolver> nonUIResolvers;

	@Override
	protected void delete() {
		getMainResolver().delete();
		commit();
	}

	@Override
	protected void addFillingUpdateTables(FillingUpdateEventArgs e) {
		e.getHandled().addResolvers(getMainResolver());
	}

	@Override
	protected void fillInsertTables(boolean isPost, HttpServletRequest request,
			DataSet postDataSet) {
		fillInsertTables(isPost, getMainResolver());
		if (!isPost && isQueryStringInput())
			ResolverUtil.setDefaultValues(getMainResolver(), request);
		if (!isPost)
			setDefaultValue(getMainResolver());
	}

	@Override
	protected void fillUpdateTables(String key, PageStyle style,
			HttpServletRequest request) {
		AbstractXmlTableResolver resolver = getMainResolver();

		if (!getFillingUpdateArgs().getHandled().getItem(resolver)) {
			// resolver.Fields = HttpGetFields;
			ResolverUtil.setKeyValues(resolver, request, key);
			if (style != PageStyle.Delete)
				resolver.addVirtualFields();
			if (style == PageStyle.Update)
				resolver.getConstraints(UpdateKind.Update);
		}
	}

	@Override
	protected void fillUpdateTables(HttpServletRequest request,
			DataSet postDataSet) {
		AbstractXmlTableResolver resolver = getMainResolver();

		if (!getFillingUpdateArgs().getHandled().getItem(resolver)) {
			ResolverUtil.setOldKeyValues(resolver, postDataSet);
			if (resolver.getHostTable().getRows().size() == 0)
				throw new NoRecordException();
		}
	}

	@Override
	public String getJScript(PageStyle style, String operation) {
		return String.format(Resource.JScriptSkeleton, "", getMainResolver()
				.getConstraints().getJavaScript());
	}

	@Override
	public AbstractXmlTableResolver getListView() {
		return mainResolver;
	}

	@Override
	protected void processCodeTables(PageStyle style) {
		ResolverUtil.processRegCodeTables(getMainResolver(), getCodeTables(),
				getCommand(), style);
	}

	public AbstractXmlTableResolver getMainResolver() {
		return this.mainResolver;
	}

	public void setMainResolver(AbstractXmlTableResolver mainResolver) {
		this.mainResolver = mainResolver;
		setTableResolverData(mainResolver);
	}

	public TableAdapterCollection<AbstractXmlTableResolver> getNonUIResolvers() {
		if (nonUIResolvers == null) {
			nonUIResolvers = new TableAdapterCollection<AbstractXmlTableResolver>(
					AbstractXmlTableResolver.class);
			nonUIResolvers.addAdapterAddedListener(this);
		}
		return nonUIResolvers;
	}

	@Override
	protected void handleInformationException(HttpServletRequest request,
			DataSet postDataSet, PageStyle style) {
		handleInformationException(request, postDataSet, style,
				getMainResolver());
	}

	@Override
	public String post(PageStyle style, String operation,
			HttpServletRequest request, DataSet postDataSet) {
		super.post(style, operation, request, postDataSet);
		switch (style) {
		case Insert:
		case Update:
			postData(style, request, postDataSet, getMainResolver());
			break;
		}
		return "";
	}

	@Override
	protected void commitData() {
		committingData();
		updateTableresolvers(getMainResolver(), nonUIResolvers);
		committedData();
	}

	@Override
	protected void internalSetDataXmlDocument(PageStyle style,
			String operation, boolean isPost, Node data) {
		getMainResolver().setDocument(style, isPost, data);
	}

	@Override
	protected void navigateData(PageStyle style) {
		getMainResolver().navigateData(style);
	}

	@Override
	protected void checkDelete(DataRightEventArgs e) {
		setRightProperties(getMainResolver(), e);
		if (!e.isChecked())
			mainResolver.checkDelete(getDataRight(), e.getRightType(), e
					.getOwnerField().toString());
	}

	@Override
	protected void checkPublic(DataRightEventArgs e) {
		setRightProperties(getMainResolver(), e);
		if (!e.isChecked())
			mainResolver.checkPublic(getDataRight(), e.getRightType(), e
					.getOwnerField().toString());
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
			mainResolver.checkReadWrite(getDataRight(), e.getRightType(), e
					.getOwnerField().toString());
	}
}
