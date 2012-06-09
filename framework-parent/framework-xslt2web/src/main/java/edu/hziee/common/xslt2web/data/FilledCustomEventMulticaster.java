package edu.hziee.common.xslt2web.data;

import java.awt.AWTEventMulticaster;
import java.util.EventListener;

class FilledCustomEventMulticaster extends AWTEventMulticaster implements
		FilledCustomListener {

	public FilledCustomEventMulticaster(EventListener a, EventListener b) {
		super(a, b);
	}

	public void filledCustomTables(FilledCustomEventArgs e) {
		if (a != null)
			((FilledCustomListener) a).filledCustomTables(e);
		if (b != null)
			((FilledCustomListener) b).filledCustomTables(e);
	}

	public static FilledCustomListener add(FilledCustomListener a,
			FilledCustomListener b) {
		return (FilledCustomListener) addInternal(a, b);
	}

	public static FilledCustomListener remove(FilledCustomListener l,
			FilledCustomListener oldl) {
		return (FilledCustomListener) removeInternal(l, oldl);
	}

	protected static EventListener addInternal(EventListener a, EventListener b) {
		if (a == null)
			return b;
		if (b == null)
			return a;
		return new NavigateDataEventMulticaster(a, b);
	}

}
