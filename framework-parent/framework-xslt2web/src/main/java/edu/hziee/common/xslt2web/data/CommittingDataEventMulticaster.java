package edu.hziee.common.xslt2web.data;

import java.awt.AWTEventMulticaster;
import java.util.EventListener;

class CommittingDataEventMulticaster extends AWTEventMulticaster implements
		CommittingDataListener {

	public CommittingDataEventMulticaster(EventListener a, EventListener b) {
		super(a, b);
	}

	public void committingData(CommittingDataEventArgs e) {
		if (a != null)
			((CommittingDataListener) a).committingData(e);
		if (b != null)
			((CommittingDataListener) b).committingData(e);
	}

	public static CommittingDataListener add(CommittingDataListener a,
			CommittingDataListener b) {
		return (CommittingDataListener) addInternal(a, b);
	}

	public static CommittingDataListener remove(CommittingDataListener l,
			CommittingDataListener oldl) {
		return (CommittingDataListener) removeInternal(l, oldl);
	}

	protected static EventListener addInternal(EventListener a, EventListener b) {
		if (a == null)
			return b;
		if (b == null)
			return a;
		return new NavigateDataEventMulticaster(a, b);
	}
}
