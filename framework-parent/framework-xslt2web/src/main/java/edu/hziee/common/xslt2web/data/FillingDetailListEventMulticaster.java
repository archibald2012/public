package edu.hziee.common.xslt2web.data;

import java.awt.AWTEventMulticaster;
import java.util.EventListener;

class FillingDetailListEventMulticaster extends AWTEventMulticaster implements
		FillingDetailListListener {

	public FillingDetailListEventMulticaster(EventListener a, EventListener b) {
		super(a, b);
	}

	public void fillingDetailListTables(FillingDetailListEventArgs e) {
		if (a != null)
			((FillingDetailListListener) a).fillingDetailListTables(e);
		if (b != null)
			((FillingDetailListListener) b).fillingDetailListTables(e);
	}

	public static FillingDetailListListener add(FillingDetailListListener a,
			FillingDetailListListener b) {
		return (FillingDetailListListener) addInternal(a, b);
	}

	public static FillingDetailListListener remove(FillingDetailListListener l,
			FillingDetailListListener oldl) {
		return (FillingDetailListListener) removeInternal(l, oldl);
	}

	protected static EventListener addInternal(EventListener a, EventListener b) {
		if (a == null)
			return b;
		if (b == null)
			return a;
		return new NavigateDataEventMulticaster(a, b);
	}
}
