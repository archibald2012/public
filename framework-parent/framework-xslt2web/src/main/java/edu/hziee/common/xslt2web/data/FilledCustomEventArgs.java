package edu.hziee.common.xslt2web.data;

import javax.servlet.http.HttpServletRequest;

import edu.hziee.common.xslt2web.sys.EventArgs;

public class FilledCustomEventArgs extends EventArgs {
	private static final long serialVersionUID = 1L;

	private boolean isPost;
	private String operation;
	private HttpServletRequest request;
	private DataSet postDataSet;

	public FilledCustomEventArgs(Object source) {
		super(source);
	}

	public final boolean isPost() {
		return isPost;
	}

	public final String getOperation() {
		return operation;
	}

	public final HttpServletRequest getRequest() {
		return request;
	}

	public final DataSet getPostDataSet() {
		return postDataSet;
	}

	void setProperties(boolean isPost, String operation,
			HttpServletRequest request, DataSet postDataSet) {
		this.isPost = isPost;
		this.operation = operation;
		this.request = request;
		this.postDataSet = postDataSet;
	}

	public static FilledCustomEventArgs getArgs(Object source, boolean isPost,
			String operation, HttpServletRequest request, DataSet postDataSet) {
		FilledCustomEventArgs result = new FilledCustomEventArgs(source);
		result.setProperties(isPost, operation, request, postDataSet);
		return result;
	}

}
