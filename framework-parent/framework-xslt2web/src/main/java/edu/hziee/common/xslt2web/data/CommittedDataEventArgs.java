package edu.hziee.common.xslt2web.data;

import edu.hziee.common.xslt2web.sys.EventArgs;

public class CommittedDataEventArgs extends EventArgs {
	private static final long serialVersionUID = 1L;

	public CommittedDataEventArgs(Object source) {
		super(source);
	}

	public static CommittedDataEventArgs getArgs(Object source) {
		return new CommittedDataEventArgs(source);
	}

}
