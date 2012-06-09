package edu.hziee.common.xslt2web.data;

import java.util.EventListener;

import edu.hziee.common.xslt2web.sys.EventArgs;

public interface NavigateDataListener extends EventListener {
	void beginNavigateData(EventArgs e);

	void endNavigateData(EventArgs e);

	void navigatingData(DataNavEventArgs e);
}
