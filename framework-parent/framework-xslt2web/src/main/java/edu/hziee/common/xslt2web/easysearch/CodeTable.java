package edu.hziee.common.xslt2web.easysearch;

import java.util.Hashtable;

import edu.hziee.common.xslt2web.configxml.MarcoConfigItem;
import edu.hziee.common.xslt2web.data.BaseDataSet;
import edu.hziee.common.xslt2web.data.DbCommand;
import edu.hziee.common.xslt2web.sys.ExpressionDataListener;
import edu.hziee.common.xslt2web.sys.IWebData;
import edu.hziee.common.xslt2web.sys.WebDataListener;

public class CodeTable {
	private String tableName;
	private String codeField;
	private String nameField;
	private MarcoConfigItem where;
	private String orderBy;
	private String sql = "";
	private CodeTableAttribute attribute;

	public CodeTable() {
	}

	public final String getTableName() {
		return tableName;
	}

	public final void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public final String getCodeField() {
		return codeField;
	}

	public final void setCodeField(String codeField) {
		this.codeField = codeField.trim();
	}

	public final String getNameField() {
		return nameField;
	}

	public final void setNameField(String nameField) {
		this.nameField = nameField.trim();
	}

	public final MarcoConfigItem getWhere() {
		return where;
	}

	public final void setWhere(MarcoConfigItem where) {
		this.where = where;
	}

	public final String getOrderBy() {
		return orderBy;
	}

	public final void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getSql(BaseDataSet dataSet) {
		if ("".equals(sql))
			sql = getSQL(dataSet);
		return sql;
	}

	public CodeTableAttribute getAttribute() {
		if (attribute == null) {
			CodeTableAnnotation annotation = this.getClass().getAnnotation(
					CodeTableAnnotation.class);
			attribute = new CodeTableAttribute();
			attribute.setValue(annotation);
		}
		return attribute;
	}

	public boolean canEasySearch() {
		return true;
	}

	private String getSQL(BaseDataSet dataSet) {
		StringBuilder builder = new StringBuilder("SELECT ");
		builder.append(
				"CODE_VALUE".equals(codeField) ? codeField : codeField
						+ " CODE_VALUE").append(", ");
		builder.append("CODE_NAME".equals(nameField) ? nameField : nameField
				+ " CODE_NAME");
		builder.append(" FROM ").append(tableName);
		if (where != null) {
			if (dataSet instanceof IWebData) {
				IWebData webData = (IWebData) dataSet;
				ExpressionDataListener listener = new WebDataListener(webData,
						dataSet);
				builder.append(" WHERE ").append(
						where.toString(webData.getAppGlobal()
								.getRegsCollection(), listener));
			}
		}
		builder.append(" ").append(orderBy);
		return builder.toString();
	}

	public void select(DbCommand command, BaseDataSet dataSet) {
		command
				.selectSql(getAttribute().getRegName(), getSql(dataSet),
						dataSet);
	}

	public void select(Hashtable<String, CodeTable> codeTables,
			DbCommand command, BaseDataSet dataSet) {
		if (!codeTables.contains(getAttribute().getRegName()))
			select(command, dataSet);
	}
}
