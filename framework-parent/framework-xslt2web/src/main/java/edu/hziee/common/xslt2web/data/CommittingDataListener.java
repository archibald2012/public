package edu.hziee.common.xslt2web.data;

import java.util.EventListener;

public interface CommittingDataListener extends EventListener {
	void committingData(CommittingDataEventArgs e);
}	
