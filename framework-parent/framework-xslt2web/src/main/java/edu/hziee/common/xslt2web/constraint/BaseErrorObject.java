package edu.hziee.common.xslt2web.constraint;

import edu.hziee.common.xslt2web.data.DataRow;
import edu.hziee.common.xslt2web.sysutil.DataSetUtil;

public class BaseErrorObject {
	private String fieldName;

	private String message;

	private int rowPosition;

	public BaseErrorObject(String fieldName, String message, int rowPosition) {
		super();
		this.fieldName = fieldName;
		this.message = message;
		this.rowPosition = rowPosition;
	}

	public final String getFieldName() {
		return fieldName;
	}

	public final String getMessage() {
		return message;
	}

	public final int getRowPosition() {
		return rowPosition;
	}

	public void addErrorData(DataRow row, String tableName) {
		DataSetUtil.setRowValues(row, new String[] { "TABLE_NAME",
				"FIELD_NAME", "ERROR_POS", "ERROR_VALUE" }, tableName,
				fieldName, rowPosition, message);
	}
}
