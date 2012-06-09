package edu.hziee.common.xslt2web.right;

import edu.hziee.common.xslt2web.sys.EventArgs;

public class DataRightEventArgs extends EventArgs {
	private static final long serialVersionUID = 1L;

	private String tableName;
	private Object rightType;
	private Object ownerField;
	private boolean checked;

	public DataRightEventArgs(Object source) {
		super(source);
	}

	public DataRightEventArgs(Object source, String tableName) {
		this(source);
		setProperty(tableName);
	}

	public void setProperty(String tableName) {
		this.tableName = tableName;
		checked = false;
	}

	public final Object getRightType() {
		return rightType;
	}

	public final void setRightType(Object rightType) {
		this.rightType = rightType;
	}

	public final Object getOwnerField() {
		return ownerField;
	}

	public final void setOwnerField(Object ownerField) {
		this.ownerField = ownerField;
	}

	public final boolean isChecked() {
		return checked;
	}

	public final void setChecked(boolean checked) {
		this.checked = checked;
	}

	public final String getTableName() {
		return tableName;
	}

}
