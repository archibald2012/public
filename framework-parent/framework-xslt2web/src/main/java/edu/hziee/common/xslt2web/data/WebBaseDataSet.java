package edu.hziee.common.xslt2web.data;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.hziee.common.xslt2web.sys.ApplicationGlobal;
import edu.hziee.common.xslt2web.sys.INeedWebData;
import edu.hziee.common.xslt2web.sys.IWebData;
import edu.hziee.common.xslt2web.sys.SessionGlobal;

public class WebBaseDataSet extends BaseDataSet implements INeedWebData,
		IWebData {
	private HttpServletRequest request;

	private HttpServletResponse response;

	private ApplicationGlobal appGlobal;

	private SessionGlobal sessionGlobal;

	public final HttpServletRequest getRequest() {
		return request;
	}

	public final HttpServletResponse getResponse() {
		return response;
	}

	public final ApplicationGlobal getAppGlobal() {
		return appGlobal;
	}

	public final SessionGlobal getSessionGlobal() {
		return sessionGlobal;
	}

	public void setData(HttpServletRequest request,
			HttpServletResponse response, ApplicationGlobal appGbl,
			SessionGlobal sessionGbl) {
		this.request = request;
		this.response = response;
		this.appGlobal = appGbl;
		this.sessionGlobal = sessionGbl;
	}
}
