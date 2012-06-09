package edu.hziee.common.xslt2web.data;

import java.awt.AWTEventMulticaster;
import java.util.EventListener;

class FilledUpdateEventMulticaster extends AWTEventMulticaster implements
		FilledUpdateListener {

	public FilledUpdateEventMulticaster(EventListener a, EventListener b) {
		super(a, b);
	}

	public void filledUpdateTables(FilledUpdateEventArgs e) {
		if (a != null)
			((FilledUpdateListener) a).filledUpdateTables(e);
		if (b != null)
			((FilledUpdateListener) b).filledUpdateTables(e);
	}

	public static FilledUpdateListener add(FilledUpdateListener a,
			FilledUpdateListener b) {
		return (FilledUpdateListener) addInternal(a, b);
	}

	public static FilledUpdateListener remove(FilledUpdateListener l,
			FilledUpdateListener oldl) {
		return (FilledUpdateListener) removeInternal(l, oldl);
	}

	protected static EventListener addInternal(EventListener a, EventListener b) {
		if (a == null)
			return b;
		if (b == null)
			return a;
		return new NavigateDataEventMulticaster(a, b);
	}
}
