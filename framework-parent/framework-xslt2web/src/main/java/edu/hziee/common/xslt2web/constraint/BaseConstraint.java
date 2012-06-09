package edu.hziee.common.xslt2web.constraint;

import edu.hziee.common.xslt2web.data.BaseDataSet;
import edu.hziee.common.xslt2web.data.DataSet;

public abstract class BaseConstraint implements Cloneable {
	private String fieldName;

	private String displayName;

	private String tableName;

	private boolean firstCheck;

	private boolean internalUse;

	private boolean serious;

	private BaseDataSet dataSet;

	private DataSet postDataSet;

	public BaseConstraint(String fieldName, String displayName) {
		super();
		this.fieldName = fieldName;
		this.displayName = displayName;
	}

	public abstract BaseErrorObject newErrorObject(int position);

	public abstract boolean checkError(String value, int position);

	public abstract String getJavaScript();

	public final boolean isFirstCheck() {
		return firstCheck;
	}

	public final void setFirstCheck(boolean firstCheck) {
		this.firstCheck = firstCheck;
	}

	public final boolean isInternalUse() {
		return internalUse;
	}

	public final void setInternalUse(boolean internalUse) {
		this.internalUse = internalUse;
	}

	public final BaseDataSet getDataSet() {
		return dataSet;
	}

	public final String getDisplayName() {
		return displayName;
	}

	public final String getFieldName() {
		return fieldName;
	}

	public final DataSet getPostDataSet() {
		return postDataSet;
	}

	public final boolean isSerious() {
		return serious;
	}

	public final String getTableName() {
		return tableName;
	}

	public final String getJSCtrlName() {
		return tableName + "." + fieldName;
	}

	void setDataSet(BaseDataSet dataSet, DataSet postDataSet) {
		this.dataSet = dataSet;
		this.postDataSet = postDataSet;

	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
