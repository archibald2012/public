package edu.hziee.common.xslt2web.data;

public interface IListDetailEvent extends IListEvent {
	void addFillingUpdateListener(FillingUpdateListener l);

	void removeFillingUpdateListener(FillingUpdateListener l);

	void addFilledUpdateListener(FilledUpdateListener l);

	void removeFilledUpdateListener(FilledUpdateListener l);

	void addFillingDetailListListener(FillingDetailListListener l);

	void removeFillingDetailListListener(FillingDetailListListener l);

	void addFilledDetailListListener(FilledDetailListListener l);

	void removeFilledDetailListListener(FilledDetailListListener l);
}
