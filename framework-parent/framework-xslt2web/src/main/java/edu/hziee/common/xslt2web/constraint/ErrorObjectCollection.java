package edu.hziee.common.xslt2web.constraint;

import java.util.ArrayList;

import edu.hziee.common.xslt2web.data.DataRow;
import edu.hziee.common.xslt2web.data.DataTable;
import edu.hziee.common.xslt2web.exception.InformationException;
import edu.hziee.common.xslt2web.sysutil.DataSetUtil;

public class ErrorObjectCollection extends ArrayList<BaseErrorObject> {
	private static final long serialVersionUID = 1L;

	private DataTable errorTable;

	private String tableName;

	public ErrorObjectCollection(String tableName) {
		super();
		this.tableName = tableName;
		errorTable = DataSetUtil.createDataTable("ERROR", "TABLE_NAME",
				"FIELD_NAME", "ERROR_VALUE", "ERROR_POS");
	}

	public final DataTable getErrorTable() {
		return errorTable;
	}

	public boolean isExists(String fieldName, int position) {
		if (fieldName == null)
			return false;
		for (BaseErrorObject errorObject : this)
			if (fieldName.equals(errorObject.getFieldName())
					&& errorObject.getRowPosition() == position)
				return true;
		return false;
	}

	private void addErrorData() {
		errorTable.getRows().clear();
		errorTable.acceptChanges();
		for (BaseErrorObject errorObject : this) {
			DataRow row = errorTable.newRow();
			errorObject.addErrorData(row, tableName);
			errorTable.getRows().add(row);
		}
		errorTable.acceptChanges();
	}

	public void checkError() {
		if (size() > 0) {
			addErrorData();
			throw new InformationException();
		}
	}
}
