package edu.hziee.common.xslt2web.data;

import java.awt.AWTEventMulticaster;
import java.util.EventListener;

class FilledDetailListEventMulticaster extends AWTEventMulticaster implements
		FilledDetailListListener {

	public FilledDetailListEventMulticaster(EventListener a, EventListener b) {
		super(a, b);
	}

	public void filledDetailListTables(FilledDetailListEventArgs e) {
		if (a != null)
			((FilledDetailListListener) a).filledDetailListTables(e);
		if (b != null)
			((FilledDetailListListener) b).filledDetailListTables(e);
	}

	public static FilledDetailListListener add(FilledDetailListListener a,
			FilledDetailListListener b) {
		return (FilledDetailListListener) addInternal(a, b);
	}

	public static FilledDetailListListener remove(FilledDetailListListener l,
			FilledDetailListListener oldl) {
		return (FilledDetailListListener) removeInternal(l, oldl);
	}

	protected static EventListener addInternal(EventListener a, EventListener b) {
		if (a == null)
			return b;
		if (b == null)
			return a;
		return new NavigateDataEventMulticaster(a, b);
	}

}
