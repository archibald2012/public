package edu.hziee.common.xslt2web.xml;

import edu.hziee.common.xslt2web.exception.ToolkitException;
import edu.hziee.common.xslt2web.sys.UserInfo;

public final class ExceptionSourceFactory {

	private ExceptionSourceFactory() {
	}

	public static final ExceptionSource createSource(String source,
			String errorPage, String url, UserInfo info, Exception ex) {
		if (ex instanceof ToolkitException)
			return new ToolkitExceptionSource(source, errorPage, url, info,
					(ToolkitException) ex);
		else
			return new ExceptionSource(source, errorPage, url, info, ex);
	}
}
