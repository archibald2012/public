package edu.hziee.common.xslt2web.data;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.right.DataRightEventArgs;
import edu.hziee.common.xslt2web.sys.IDetailPage;
import edu.hziee.common.xslt2web.sys.PageStyle;

public abstract class WebListDetailDataSet extends WebListDataSet implements
		IDetailPage, IListDetailEvent {
	private TableAdapterCollection<AbstractXmlTableResolver> detailResolvers;
	private TableRelationCollection relations;
	private FillingUpdateEventArgs fillingUpdateArgs;
	private FillingDetailListEventArgs fillingDetailListArgs;
	private FillingUpdateListener fillingUpdateListener;
	private FilledUpdateListener filledUpdateListener;
	private FillingDetailListListener fillingDetailListListener;
	private FilledDetailListListener filledDetailListListener;

	public final synchronized void addFilledDetailListListener(
			FilledDetailListListener l) {
		filledDetailListListener = FilledDetailListEventMulticaster.add(
				filledDetailListListener, l);
	}

	public final synchronized void addFilledUpdateListener(FilledUpdateListener l) {
		filledUpdateListener = FilledUpdateEventMulticaster.add(
				filledUpdateListener, l);
	}

	public final synchronized void addFillingDetailListListener(
			FillingDetailListListener l) {
		fillingDetailListListener = FillingDetailListEventMulticaster.add(
				fillingDetailListListener, l);
	}

	public final synchronized void addFillingUpdateListener(FillingUpdateListener l) {
		fillingUpdateListener = FillingUpdateEventMulticaster.add(
				fillingUpdateListener, l);
	}

	public final synchronized void removeFilledDetailListListener(
			FilledDetailListListener l) {
		filledDetailListListener = FilledDetailListEventMulticaster.remove(
				filledDetailListListener, l);
	}

	public final synchronized void removeFilledUpdateListener(FilledUpdateListener l) {
		filledUpdateListener = FilledUpdateEventMulticaster.remove(
				filledUpdateListener, l);
	}

	public final synchronized void removeFillingDetailListListener(
			FillingDetailListListener l) {
		fillingDetailListListener = FillingDetailListEventMulticaster.remove(
				fillingDetailListListener, l);
	}

	public final synchronized void removeFillingUpdateListener(FillingUpdateListener l) {
		fillingUpdateListener = FillingUpdateEventMulticaster.remove(
				fillingUpdateListener, l);
	}

	final FillingUpdateEventArgs getFillingUpdateArgs() {
		return fillingUpdateArgs;
	}

	final FillingDetailListEventArgs getFillingDetailListArgs() {
		return fillingDetailListArgs;
	}

	public final TableAdapterCollection<AbstractXmlTableResolver> getDetailResolvers() {
		if (detailResolvers == null) {
			detailResolvers = new TableAdapterCollection<AbstractXmlTableResolver>(
					AbstractXmlTableResolver.class);
			detailResolvers.addAdapterAddedListener(this);
		}
		return detailResolvers;
	}

	public final TableRelationCollection getRelations() {
		if (relations == null)
			relations = new TableRelationCollection();
		return relations;
	}

	protected void onFilledDetailListTables(FilledDetailListEventArgs e) {
		if (filledDetailListListener != null)
			filledDetailListListener.filledDetailListTables(e);
	}

	protected void onFillingDetailListTables(FillingDetailListEventArgs e) {
		if (fillingDetailListListener != null)
			fillingDetailListListener.fillingDetailListTables(e);
	}

	protected void onFilledUpdateTables(FilledUpdateEventArgs e) {
		if (filledUpdateListener != null)
			filledUpdateListener.filledUpdateTables(e);
	}

	protected void onFillingUpdateTables(FillingUpdateEventArgs e) {
		if (fillingUpdateListener != null)
			fillingUpdateListener.fillingUpdateTables(e);
	}

	protected void addFillingUpdateTables(FillingUpdateEventArgs e) {
		e.getHandled().addResolvers(getMainResolver());
	}

	protected void addFillingDetailListTables(FillingDetailListEventArgs e) {
		if (relations != null)
			relations.addFillingResolver(e.getHandled());
	}

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

	@Override
	protected void navigateData(PageStyle style) {
		super.navigateData(style);

		if (style == PageStyle.Detail && detailResolvers != null)
			for (AbstractXmlTableResolver resolver : detailResolvers)
				resolver.navigateData(style);
	}

	@Override
	protected void processCodeTables(PageStyle style) {
		super.processCodeTables(style);

		if (style == PageStyle.Detail && detailResolvers != null)
			for (AbstractXmlTableResolver resolver : detailResolvers)
				ResolverUtil.processRegCodeTables(resolver, getCodeTables(),
						getCommand(), style);
	}

	@Override
	public void setData(boolean isPost, PageStyle style, String operation,
			HttpServletRequest request) {
		switch (style) {
		case List:
			super.setData(isPost, style, operation, request);
			break;
		case Detail:
			setDataXmlDocument(style, operation, isPost);
			setDetailData(request);
			break;
		}
	}

	private void setDetailData(HttpServletRequest request) {
		Object key = request.getParameter("ID");
		fillingUpdateEvent(false, key, PageStyle.Detail, request, null);
		fillUpdateTables(key, PageStyle.Detail, request);
		onFilledUpdateTables(FilledUpdateEventArgs.getArgs(this, false, key,
				PageStyle.Detail, request, null));

		if (isSupportData())
			checkPublic(new DataRightEventArgs(this));

		fillingDetailListEvent();
		fillDetailListTables();
		onFilledDetailListTables(FilledDetailListEventArgs.getArgs(this));

		navigateData(PageStyle.Detail);
		processCodeTables(PageStyle.Detail);
	}

	protected void checkPublic(DataRightEventArgs e) {
		e.setProperty(getMainResolver().getTableName());
		setRightProperties(getMainResolver(), e);
		if (!e.isChecked())
			getMainResolver().checkPublic(getDataRight(), e.getRightType(),
					e.getOwnerField().toString());
	}

	protected void fillDetailListTables() {
		if (relations != null)
			relations.fillDetailTable(getFillingDetailListArgs().getHandled());
	}

	protected void fillUpdateTables(Object key, PageStyle style,
			HttpServletRequest request) {
		AbstractXmlTableResolver resolver = getMainResolver();

		if (!getFillingUpdateArgs().getHandled().getItem(resolver)) {
			// resolver.Fields = HttpGetFields;
			ResolverUtil.setKeyValues(resolver, request, key);
			resolver.addVirtualFields();
		}
	}

	@Override
	protected void internalSetDataXmlDocument(PageStyle style,
			String operation, boolean isPost, Node data) {
		switch (style) {
		case List:
			super.internalSetDataXmlDocument(style, operation, isPost, data);
			break;
		case Detail:
			super.internalSetDataXmlDocument(style, operation, isPost, data);
			if (detailResolvers != null)
				for (AbstractXmlTableResolver resolver : detailResolvers)
					resolver.setDocument(style, isPost, data);
			break;
		}
	}
}
