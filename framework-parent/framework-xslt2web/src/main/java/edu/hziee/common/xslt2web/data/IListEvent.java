package edu.hziee.common.xslt2web.data;

public interface IListEvent {
	void addFilledCustomListener(FilledCustomListener l);

	void removeFilledCustomListener(FilledCustomListener l);

	void addFilledListListener(FilledListListener l);

	void removeFilledListListener(FilledListListener l);
}
