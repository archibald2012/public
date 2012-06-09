package edu.hziee.common.xslt2web.exception;

public class RedirectException extends BaseException {
	private static final long serialVersionUID = 1L;

	private String url;

	public RedirectException(String url) {
		super();
		this.url = url;
	}

	public final String getUrl() {
		return url;
	}

	public final void setUrl(String url) {
		this.url = url;
	}

}
