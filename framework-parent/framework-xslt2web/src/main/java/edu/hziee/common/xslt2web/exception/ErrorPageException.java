package edu.hziee.common.xslt2web.exception;

import edu.hziee.common.xslt2web.sysutil.DataSetUtil;
import edu.hziee.common.xslt2web.sysutil.Resource;

public class ErrorPageException extends BaseException {

	private String errorTitle;
	private String errorBody;
	private String pageTitle;
	private String retURL;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ErrorPageException() {
		super();
		this.retURL = "";
	}

	public ErrorPageException(String pageTitle, String errorTitle,
			String errorBody) {
		this();
		this.errorTitle = errorTitle;
		this.errorBody = errorBody;
		this.pageTitle = pageTitle;
	}

	public ErrorPageException(String pageTitle, String errorTitle,
			String errorBody, String retURL) {
		this(pageTitle, errorTitle, errorBody);
		this.retURL = retURL;
	}

	public ErrorPageException(String pageTitle, String errorBody) {
		this(pageTitle, pageTitle, errorBody);
	}

	public final String getErrorTitle() {
		return errorTitle;
	}

	public final void setErrorTitle(String errorTitle) {
		this.errorTitle = errorTitle;
	}

	public final String getErrorBody() {
		return errorBody;
	}

	public final void setErrorBody(String errorBody) {
		this.errorBody = errorBody;
	}

	public final String getPageTitle() {
		return pageTitle;
	}

	public final void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public final String getRetURL() {
		return retURL;
	}

	public final void setRetURL(String retURL) {
		this.retURL = retURL;
	}

	public final String getXmlString() {
		return String.format(Resource.ErrorPageXml, DataSetUtil
				.escapeString(pageTitle), DataSetUtil.escapeString(errorTitle),
				DataSetUtil.escapeString(errorBody), DataSetUtil
						.escapeString(retURL));
	}
}
