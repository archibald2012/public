package edu.hziee.common.xslt2web.data;

import java.awt.AWTEventMulticaster;
import java.util.EventListener;

class FilledListEventMulticaster extends AWTEventMulticaster implements
		FilledListListener {

	public FilledListEventMulticaster(EventListener a, EventListener b) {
		super(a, b);
	}

	public void filledListTables(FilledListEventArgs e) {
		if (a != null)
			((FilledListListener) a).filledListTables(e);
		if (b != null)
			((FilledListListener) b).filledListTables(e);
	}

	public static FilledListListener add(FilledListListener a,
			FilledListListener b) {
		return (FilledListListener) addInternal(a, b);
	}

	public static FilledListListener remove(FilledListListener l,
			FilledListListener oldl) {
		return (FilledListListener) removeInternal(l, oldl);
	}

	protected static EventListener addInternal(EventListener a, EventListener b) {
		if (a == null)
			return b;
		if (b == null)
			return a;
		return new NavigateDataEventMulticaster(a, b);
	}

}
