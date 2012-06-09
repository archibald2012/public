package edu.hziee.common.xslt2web.sys;


public interface IXmlPage extends IHttpGetPage {
	String getXsltFile(boolean isIe, PageStyle style, String operation);

	String getDefaultPage(boolean isPost, PageStyle style, String operation,
			String retURL);
}
