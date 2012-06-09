package edu.hziee.common.xslt2web.data;

import java.util.EventListener;

public interface FilledInsertListener extends EventListener {
	void filledInsertTables(FilledInsertEventArgs e);
}
