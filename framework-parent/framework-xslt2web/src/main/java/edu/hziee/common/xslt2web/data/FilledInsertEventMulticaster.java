package edu.hziee.common.xslt2web.data;

import java.awt.AWTEventMulticaster;
import java.util.EventListener;

class FilledInsertEventMulticaster extends AWTEventMulticaster implements
		FilledInsertListener {

	public FilledInsertEventMulticaster(EventListener a, EventListener b) {
		super(a, b);
		// TODO Auto-generated constructor stub
	}

	public void filledInsertTables(FilledInsertEventArgs e) {
		if (a != null)
			((FilledInsertListener) a).filledInsertTables(e);
		if (b != null)
			((FilledInsertListener) b).filledInsertTables(e);
	}

	public static FilledInsertListener add(FilledInsertListener a,
			FilledInsertListener b) {
		return (FilledInsertListener) addInternal(a, b);
	}

	public static FilledInsertListener remove(FilledInsertListener l,
			FilledInsertListener oldl) {
		return (FilledInsertListener) removeInternal(l, oldl);
	}

	protected static EventListener addInternal(EventListener a, EventListener b) {
		if (a == null)
			return b;
		if (b == null)
			return a;
		return new NavigateDataEventMulticaster(a, b);
	}

}
