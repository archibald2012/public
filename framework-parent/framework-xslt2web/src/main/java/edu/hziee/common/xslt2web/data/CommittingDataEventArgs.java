package edu.hziee.common.xslt2web.data;

import edu.hziee.common.xslt2web.sys.EventArgs;

public class CommittingDataEventArgs extends EventArgs {
	private static final long serialVersionUID = 1L;

	public CommittingDataEventArgs(Object source) {
		super(source);
	}

	public static CommittingDataEventArgs getArgs(Object source) {
		return new CommittingDataEventArgs(source);
	}
}
