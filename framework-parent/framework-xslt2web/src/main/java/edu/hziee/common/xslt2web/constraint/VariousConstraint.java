package edu.hziee.common.xslt2web.constraint;

import edu.hziee.common.xslt2web.data.BaseDataSet;
import edu.hziee.common.xslt2web.data.DBNull;
import edu.hziee.common.xslt2web.data.DataRow;
import edu.hziee.common.xslt2web.data.DataTable;
import edu.hziee.common.xslt2web.data.DbCommand;
import edu.hziee.common.xslt2web.sysutil.DataSetUtil;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

public abstract class VariousConstraint extends BaseConstraint {
	private String message;

	private String hdFieldValue;

	public VariousConstraint(String fieldName, String displayName) {
		super(fieldName, displayName);
		setFirstCheck(true);
	}

	private DataTable getErrorTable() {
		DataTable errTable = getDataSet().getTables().getItem(getFieldName());
		if (errTable == null)
			errTable = createErrorTable();

		return errTable;
	}

	private boolean checkXmlHttpError(String value, int position) {
		boolean noError = true;
		DataRow row = getPostDataSet().getTables().getItem(getTableName())
				.getRows().getItem(position);
		try {
			hdFieldValue = row.getItem("hd" + getFieldName()).toString();
		} catch (Exception ex) {
			return true;
		}
		if ("hd~SELECT~".equals(hdFieldValue) || "".equals(value)) {
			hdFieldValue = row.getItem(getFieldName()).toString();
			setHdFieldValue(position, hdFieldValue);

			if (!"".equals(value)) {
				BaseDataSet existDataSet = new BaseDataSet();
				getDataSet().setConnectionString(existDataSet);
				DbCommand selector = existDataSet.getConnection()
						.createCommand();
				// TableSelector selector = new TableSelector(existDataSet);

				setInfoData(selector, existDataSet, getFieldName(),
						hdFieldValue, position);

				DataTable errTable = getErrorTable();
				errTable.getRows().add(
						new Object[] {
								hdFieldValue,
								existDataSet.getTables()
										.getItem(getFieldName()).getRows()
										.getItem(0).getItem("CODE_NAME"),
								position });

			}
			return noError;
		}
		if (!StringUtil.isEmpty(hdFieldValue))
			noError = checkExist(value, position);
		if (StringUtil.isEmpty(hdFieldValue))
			noError = checkVarious(value, position);
		if (noError)
			setHdFieldValue(position, hdFieldValue);
		return noError;
	}

	private DataTable createErrorTable() {
		DataTable table = DataSetUtil.createDataTable(getFieldName(),
				"CODE_VALUE", "CODE_NAME", "POS");
		getDataSet().getTables().add(table);
		return table;
	}

	private boolean checkExist(String value, int position) {
		BaseDataSet existDataSet = new BaseDataSet();
		getDataSet().setConnectionString(existDataSet);
		DbCommand selector = existDataSet.getConnection().createCommand();
		// TableSelector selector = new TableSelector(existDataSet);

		boolean isOpen = setExistData(selector, existDataSet, getFieldName(),
				value, hdFieldValue, position);

		DataTable table = existDataSet.getTables().getItem(getFieldName());
		if (!isOpen || table.getRows().size() == 0) {
			hdFieldValue = "";
			message = String.format("%s不存在", getDisplayName());
			return false;
		} else {
			hdFieldValue = table.getRows().getItem(0).getItem("CODE_VALUE")
					.toString();
			DataTable errDataTable = getErrorTable();
			errDataTable.getRows().add(
					new Object[] { hdFieldValue, value, position });
			return true;
		}
	}

	private boolean checkVarious(String value, int position) {
		BaseDataSet existDataSet = new BaseDataSet();
		getDataSet().setConnectionString(existDataSet);
		DbCommand selector = existDataSet.getConnection().createCommand();
		// TableSelector selector = new TableSelector(existDataSet);

		setVariousData(selector, existDataSet, getFieldName(), value, position);

		DataTable table = existDataSet.getTables().getItem(getFieldName());
		DataTable errDataTable = getErrorTable();

		switch (table.getRows().size()) {
		case 0:
			message = String.format("%s不存在", getDisplayName());
			hdFieldValue = "";
			errDataTable.getRows().add(new Object[] { value, value, position });
			return false;
		case 1:
			message = "";
			hdFieldValue = table.getRows().getItem(0).getItem("CODE_VALUE")
					.toString();
			errDataTable.getRows().add(
					new Object[] { hdFieldValue, value, position });
			return true;
		default:
			message = "数据有二义性";
			hdFieldValue = "";
			for (DataRow row : table.getRows())
				errDataTable.getRows().add(
						new Object[] { row.getItem("CODE_VALUE"),
								row.getItem("CODE_NAME"), position });
			return false;
		}
	}

	protected abstract boolean setVariousData(DbCommand command,
			BaseDataSet dataSet, String tableName, String value, int position);

	protected abstract boolean setExistData(DbCommand command,
			BaseDataSet dataSet, String tableName, String value,
			String valueID, int position);

	protected abstract boolean setInfoData(DbCommand command,
			BaseDataSet dataSet, String tableName, String value, int position);

	protected void setHdFieldValue(int position, String hdFieldValue) {
		if (StringUtil.isEmpty(hdFieldValue))
			getPostDataSet().getTables().getItem(getTableName()).getRows()
					.getItem(position).setItem(getFieldName(), DBNull.value);
		else
			getPostDataSet().getTables().getItem(getTableName()).getRows()
					.getItem(position).setItem(getFieldName(), hdFieldValue);
	}

	@Override
	public boolean checkError(String value, int position) {
		return checkXmlHttpError(value, position);
	}

	@Override
	public String getJavaScript() {
		return "";
	}

	@Override
	public BaseErrorObject newErrorObject(int position) {
		return new BaseErrorObject(getFieldName(), message, position);
	}

}
