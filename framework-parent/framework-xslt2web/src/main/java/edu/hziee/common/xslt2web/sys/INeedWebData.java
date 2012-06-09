package edu.hziee.common.xslt2web.sys;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface INeedWebData {
	void setData(HttpServletRequest request, HttpServletResponse response,
			ApplicationGlobal appGbl, SessionGlobal sessionGbl);
}
