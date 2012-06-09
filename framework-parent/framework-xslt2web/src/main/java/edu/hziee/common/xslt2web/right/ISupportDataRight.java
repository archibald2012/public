package edu.hziee.common.xslt2web.right;

public interface ISupportDataRight {
	boolean isSupportData();

	void setSupportData(boolean supportData);

	void setRightProperties(Object data, DataRightEventArgs e);
}
