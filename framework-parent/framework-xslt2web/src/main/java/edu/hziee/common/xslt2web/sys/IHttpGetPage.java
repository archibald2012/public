package edu.hziee.common.xslt2web.sys;

import javax.servlet.http.HttpServletRequest;

public interface IHttpGetPage {
	void setData(boolean isPost, PageStyle style, String operation,
			HttpServletRequest request);
}
