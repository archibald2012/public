package edu.hziee.common.xslt2web.data;

import javax.servlet.http.HttpServletRequest;

import edu.hziee.common.xslt2web.sys.PageStyle;

public class FillingUpdateEventArgs extends FilledUpdateEventArgs {
	private static final long serialVersionUID = 1L;

	private HandledCollection handled;

	public FillingUpdateEventArgs(Object source) {
		super(source);
		handled = new HandledCollection();
	}

	public final HandledCollection getHandled() {
		return handled;
	}

	public static FillingUpdateEventArgs getArgs(Object source, boolean isPost,
			Object key, PageStyle style, HttpServletRequest request,
			DataSet postDataSet) {
		FillingUpdateEventArgs result = new FillingUpdateEventArgs(source);
		result.setProperties(isPost, key, style, request, postDataSet);
		return result;
	}
}
