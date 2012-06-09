package edu.hziee.common.xslt2web.sys;

import javax.servlet.http.HttpServletRequest;


public interface IHttpPostPage extends IXmlPage {
	String getJScript(PageStyle style, String operation);

	String post(PageStyle style, String operation, HttpServletRequest request);
}
