package edu.hziee.common.xslt2web.data;

import java.awt.AWTEventMulticaster;
import java.util.EventListener;

import edu.hziee.common.xslt2web.sys.EventArgs;

class NavigateDataEventMulticaster extends AWTEventMulticaster implements
		NavigateDataListener {

	public NavigateDataEventMulticaster(EventListener a, EventListener b) {
		super(a, b);
	}

	public void beginNavigateData(EventArgs e) {
		if (a != null)
			((NavigateDataListener) a).beginNavigateData(e);
		if (b != null)
			((NavigateDataListener) b).beginNavigateData(e);
	}

	public void endNavigateData(EventArgs e) {
		if (a != null)
			((NavigateDataListener) a).endNavigateData(e);
		if (b != null)
			((NavigateDataListener) b).endNavigateData(e);
	}

	public void navigatingData(DataNavEventArgs e) {
		if (a != null)
			((NavigateDataListener) a).navigatingData(e);
		if (b != null)
			((NavigateDataListener) b).navigatingData(e);
	}

	public static NavigateDataListener add(NavigateDataListener a,
			NavigateDataListener b) {
		return (NavigateDataListener) addInternal(a, b);
	}

	public static NavigateDataListener remove(NavigateDataListener l,
			NavigateDataListener oldl) {
		return (NavigateDataListener) removeInternal(l, oldl);
	}

	protected static EventListener addInternal(EventListener a, EventListener b) {
		if (a == null)
			return b;
		if (b == null)
			return a;
		return new NavigateDataEventMulticaster(a, b);
	}
}
