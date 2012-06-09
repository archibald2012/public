package edu.hziee.common.xslt2web.data;

import java.awt.AWTEventMulticaster;
import java.util.EventListener;

class CommittedDataEventMulticaster extends AWTEventMulticaster implements
		CommittedDataListener {

	public CommittedDataEventMulticaster(EventListener a, EventListener b) {
		super(a, b);
	}

	public void committedData(CommittedDataEventArgs e) {
		if (a != null)
			((CommittedDataListener) a).committedData(e);
		if (b != null)
			((CommittedDataListener) b).committedData(e);
	}

	public static CommittedDataListener add(CommittedDataListener a,
			CommittedDataListener b) {
		return (CommittedDataListener) addInternal(a, b);
	}

	public static CommittedDataListener remove(CommittedDataListener l,
			CommittedDataListener oldl) {
		return (CommittedDataListener) removeInternal(l, oldl);
	}

	protected static EventListener addInternal(EventListener a, EventListener b) {
		if (a == null)
			return b;
		if (b == null)
			return a;
		return new NavigateDataEventMulticaster(a, b);
	}
}
