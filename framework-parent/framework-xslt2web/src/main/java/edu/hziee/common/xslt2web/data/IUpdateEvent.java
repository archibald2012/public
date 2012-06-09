package edu.hziee.common.xslt2web.data;

public interface IUpdateEvent extends IListDetailEvent {
	void addFilledInsertListener(FilledInsertListener l);

	void removeFilledInsertListener(FilledInsertListener l);

	void addCommittingDataListener(CommittingDataListener l);

	void removeCommittingDataListener(CommittingDataListener l);

	void addCommittedDataListener(CommittedDataListener l);

	void removeCommittedDataListener(CommittedDataListener l);
}
