package edu.hziee.common.xslt2web.data;

import javax.servlet.http.HttpServletRequest;

import edu.hziee.common.xslt2web.sys.EventArgs;

public class FilledInsertEventArgs extends EventArgs {
	private static final long serialVersionUID = 1L;

	private boolean isPost;
	private HttpServletRequest request;
	private DataSet postDataSet;

	public FilledInsertEventArgs(Object source) {
		super(source);
	}

	public final boolean isPost() {
		return isPost;
	}

	public final HttpServletRequest getRequest() {
		return request;
	}

	public final DataSet getPostDataSet() {
		return postDataSet;
	}

	void setProperties(boolean isPost, HttpServletRequest request,
			DataSet postDataSet) {
		this.isPost = isPost;
		this.request = request;
		this.postDataSet = postDataSet;
	}

	public static FilledInsertEventArgs getArgs(Object source, boolean isPost,
			HttpServletRequest request, DataSet postDataSet) {
		FilledInsertEventArgs result = new FilledInsertEventArgs(source);
		result.setProperties(isPost, request, postDataSet);
		return result;
	}
}
