package edu.hziee.common.xslt2web.data;

import edu.hziee.common.xslt2web.sys.EventArgs;

public class DataNavEventArgs extends EventArgs {
	private static final long serialVersionUID = 1L;
	private DataRow row;

	public DataNavEventArgs(Object source) {
		super(source);
	}

	public final DataRow getRow() {
		return row;
	}

	final void setRow(DataRow row) {
		this.row = row;
	}
}
