package edu.hziee.common.xslt2web.data;

import java.util.EnumSet;

import javax.servlet.http.HttpServletRequest;

import edu.hziee.common.xslt2web.sys.PageStyle;

public abstract class WebConfigDataSet extends WebSingleDataSet {

	public WebConfigDataSet() {
		super();
		setDisablePage(EnumSet.of(DisableFunction.Delete,
				DisableFunction.Insert, DisableFunction.List));
		setSupportData(false);
	}

	@Override
	protected void fillUpdateTables(HttpServletRequest request,
			DataSet postDataSet) {
		AbstractXmlTableResolver resolver = getMainResolver();

		if (!getFillingUpdateArgs().getHandled().getItem(resolver))
			resolver.select("");
	}

	@Override
	protected void fillUpdateTables(String key, PageStyle style,
			HttpServletRequest request) {
		AbstractXmlTableResolver resolver = getMainResolver();

		if (!getFillingUpdateArgs().getHandled().getItem(resolver)) {
			resolver.select("");
			resolver.addVirtualFields();
			resolver.getConstraints(UpdateKind.Update);
		}
	}

	@Override
	public String getDefaultPage(boolean isPost, PageStyle style,
			String operation, String retURL) {
		return String.format("../toolkit/webdetailpage?Source=%s&ID=-1",
				getAnnotation().regName());
	}

}
