package edu.hziee.common.xslt2web.data;

import java.util.EventListener;

public interface CommittedDataListener extends EventListener {
	void committedData(CommittedDataEventArgs e);
}
