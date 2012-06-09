package edu.hziee.common.xslt2web.data;

import javax.servlet.http.HttpServletRequest;

import edu.hziee.common.xslt2web.sys.EventArgs;
import edu.hziee.common.xslt2web.sys.PageStyle;

public class FilledUpdateEventArgs extends EventArgs {
	private static final long serialVersionUID = 1L;

	private boolean isPost;
	private Object key;
	private PageStyle style;
	private HttpServletRequest request;
	private DataSet postDataSet;

	public FilledUpdateEventArgs(Object source) {
		super(source);
	}

	public final boolean isPost() {
		return isPost;
	}

	public final Object getKey() {
		return key;
	}

	public final PageStyle getStyle() {
		return style;
	}

	public final HttpServletRequest getRequest() {
		return request;
	}

	public final DataSet getPostDataSet() {
		return postDataSet;
	}

	void setProperties(boolean isPost, Object key, PageStyle style,
			HttpServletRequest request, DataSet postDataSet) {
		this.isPost = isPost;
		this.key = key;
		this.style = style;
		this.request = request;
		this.postDataSet = postDataSet;
	}

	public static FilledUpdateEventArgs getArgs(Object source, boolean isPost,
			Object key, PageStyle style, HttpServletRequest request,
			DataSet postDataSet) {
		FilledUpdateEventArgs result = new FilledUpdateEventArgs(source);
		result.setProperties(isPost, key, style, request, postDataSet);
		return result;
	}
}
