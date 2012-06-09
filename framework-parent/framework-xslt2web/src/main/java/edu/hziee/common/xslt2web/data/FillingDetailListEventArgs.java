package edu.hziee.common.xslt2web.data;

public class FillingDetailListEventArgs extends FilledDetailListEventArgs {
	private static final long serialVersionUID = 1L;
	private HandledCollection handled;

	public FillingDetailListEventArgs(Object source) {
		super(source);
		handled = new HandledCollection();
	}

	public final HandledCollection getHandled() {
		return handled;
	}

	public static FillingDetailListEventArgs getArgs(Object source) {
		FillingDetailListEventArgs result = new FillingDetailListEventArgs(
				source);
		return result;
	}
}
