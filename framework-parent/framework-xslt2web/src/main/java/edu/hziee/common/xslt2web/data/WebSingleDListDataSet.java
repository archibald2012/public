package edu.hziee.common.xslt2web.data;

import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.sys.PageStyle;

public abstract class WebSingleDListDataSet extends WebSingleDataSet {
	private TableAdapterCollection<AbstractXmlTableResolver> detailResolvers;
	private TableRelationCollection relations;

	public WebSingleDListDataSet() {
		super();
		detailResolvers = new TableAdapterCollection<AbstractXmlTableResolver>(
				AbstractXmlTableResolver.class);
		detailResolvers.addAdapterAddedListener(this);
		relations = new TableRelationCollection();
	}

	public final TableAdapterCollection<AbstractXmlTableResolver> getDetailResolvers() {
		return detailResolvers;
	}

	public final TableRelationCollection getRelations() {
		return relations;
	}

	@Override
	protected void navigateData(PageStyle style) {
		super.navigateData(style);
		if (style == PageStyle.Detail)
			for (AbstractXmlTableResolver resolver : getDetailResolvers())
				resolver.navigateData(style);
	}

	@Override
	protected void processCodeTables(PageStyle style) {
		super.processCodeTables(style);

		if (style == PageStyle.Detail)
			for (AbstractXmlTableResolver resolver : getDetailResolvers())
				ResolverUtil.processRegCodeTables(resolver, getCodeTables(),
						getCommand(), style);

	}

	protected void addFillingDetailListTables(FillingDetailListEventArgs e) {
		relations.addFillingResolver(getFillingDetailListArgs().getHandled());
	}

	@Override
	protected void fillDetailListTables() {
		relations.fillDetailTable(getFillingDetailListArgs().getHandled());
		// relations.fillDetailTable();
	}

	@Override
	protected void internalSetDataXmlDocument(PageStyle style,
			String operation, boolean isPost, Node data) {
		switch (style) {
		case Insert:
		case Update:
		case Delete:
			getMainResolver().setDocument(style, isPost, data);
			break;
		case List:
			getListView().setDocument(style, isPost, data);
			break;
		case Detail:
			getMainResolver().setDocument(style, isPost, data);
			for (AbstractXmlTableResolver resolver : getDetailResolvers())
				resolver.setDocument(style, isPost, data);
			break;
		}
	}
}
