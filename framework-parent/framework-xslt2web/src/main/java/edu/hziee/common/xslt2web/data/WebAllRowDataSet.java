package edu.hziee.common.xslt2web.data;

import java.util.EnumSet;

import javax.servlet.http.HttpServletRequest;

import edu.hziee.common.xslt2web.sys.PageStyle;

public abstract class WebAllRowDataSet extends WebSingleDataSet {

	public WebAllRowDataSet() {
		super();
		setDisablePage(EnumSet.of(DisableFunction.Delete,
				DisableFunction.Insert, DisableFunction.Detail));
		setSupportData(false);
	}

	@Override
	protected void fillUpdateTables(String key, PageStyle style,
			HttpServletRequest request) {
		getMainResolver().select("");
		getMainResolver().getConstraints(UpdateKind.Update);
	}

	@Override
	public void setMainResolver(AbstractXmlTableResolver mainResolver) {
		super.setMainResolver(mainResolver);
		getMainResolver().setUpdateMode(UpdateMode.Merge);
	}

}
