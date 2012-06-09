package edu.hziee.common.xslt2web.xml;

import edu.hziee.common.xslt2web.exception.ErrorPageException;

public class ErrorPageSource extends XmlEmptySource {
    private ErrorPageException errorPage;

	public final ErrorPageException getErrorPage() {
		return errorPage;
	}

	public final void setErrorPage(ErrorPageException errorPage) {
		this.errorPage = errorPage;
	}

	@Override
	public String getXmlString() {
		setContent(errorPage.getXmlString());
		return super.getXmlString();
	}
}
