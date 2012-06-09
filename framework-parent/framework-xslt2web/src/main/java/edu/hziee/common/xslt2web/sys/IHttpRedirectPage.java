package edu.hziee.common.xslt2web.sys;


public interface IHttpRedirectPage extends IHttpGetPage {
	String getDefaultPage(boolean isPost, PageStyle style, String operation,
			String retURL);

}
