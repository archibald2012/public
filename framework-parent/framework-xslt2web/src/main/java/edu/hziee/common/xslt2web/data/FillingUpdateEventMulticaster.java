package edu.hziee.common.xslt2web.data;

import java.awt.AWTEventMulticaster;
import java.util.EventListener;

class FillingUpdateEventMulticaster extends AWTEventMulticaster
		implements FillingUpdateListener {

	public FillingUpdateEventMulticaster(EventListener a, EventListener b) {
		super(a, b);
	}

	public void fillingUpdateTables(FillingUpdateEventArgs e) {
		if (a != null)
			((FillingUpdateListener) a).fillingUpdateTables(e);
		if (b != null)
			((FillingUpdateListener) b).fillingUpdateTables(e);
	}

	public static FillingUpdateListener add(FillingUpdateListener a,
			FillingUpdateListener b) {
		return (FillingUpdateListener) addInternal(a, b);
	}

	public static FillingUpdateListener remove(FillingUpdateListener l,
			FillingUpdateListener oldl) {
		return (FillingUpdateListener) removeInternal(l, oldl);
	}

	protected static EventListener addInternal(EventListener a, EventListener b) {
		if (a == null)
			return b;
		if (b == null)
			return a;
		return new NavigateDataEventMulticaster(a, b);
	}
}
