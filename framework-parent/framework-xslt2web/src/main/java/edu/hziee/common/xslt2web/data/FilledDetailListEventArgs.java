package edu.hziee.common.xslt2web.data;

import edu.hziee.common.xslt2web.sys.EventArgs;

public class FilledDetailListEventArgs extends EventArgs {
	private static final long serialVersionUID = 1L;

	public FilledDetailListEventArgs(Object source) {
		super(source);
	}

	public static FilledDetailListEventArgs getArgs(Object source) {
		return new FilledDetailListEventArgs(source);
	}
}
