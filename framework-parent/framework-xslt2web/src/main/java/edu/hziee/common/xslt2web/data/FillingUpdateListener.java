package edu.hziee.common.xslt2web.data;

import java.util.EventListener;

public interface FillingUpdateListener extends EventListener {
	void fillingUpdateTables(FillingUpdateEventArgs e);
}
