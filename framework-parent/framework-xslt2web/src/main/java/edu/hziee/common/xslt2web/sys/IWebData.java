package edu.hziee.common.xslt2web.sys;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IWebData {
	HttpServletRequest getRequest();
	HttpServletResponse getResponse();
	ApplicationGlobal getAppGlobal();
	SessionGlobal getSessionGlobal();
}
