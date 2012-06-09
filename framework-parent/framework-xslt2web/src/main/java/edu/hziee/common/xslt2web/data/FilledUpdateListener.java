package edu.hziee.common.xslt2web.data;

import java.util.EventListener;

public interface FilledUpdateListener extends EventListener {
	void filledUpdateTables(FilledUpdateEventArgs e);
}
