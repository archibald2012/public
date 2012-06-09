package edu.hziee.common.xslt2web.xml;

import edu.hziee.common.xslt2web.data.DataRow;
import edu.hziee.common.xslt2web.exception.ToolkitException;
import edu.hziee.common.xslt2web.sys.UserInfo;

public class ToolkitExceptionSource extends ExceptionSource {
	private ToolkitException toolkitException;

	ToolkitExceptionSource(String source, String errorPage, String url,
			UserInfo info, ToolkitException ex) {
		super(source, errorPage, url, info, ex);
	}

	@Override
	protected void handleException(DataRow row, Exception ex) {
		this.toolkitException = (ToolkitException) ex;
		row.setItem("Message", toolkitException.getMessage());
		row.setItem("StackTrace",
				getStackTrace(toolkitException.getException()));
		row.setItem("Type", toolkitException.getException().getClass()
				.toString());
		row.setItem("ErrorSource", toolkitException.getSourceInfo());
	}

}
