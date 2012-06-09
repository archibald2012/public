package edu.hziee.common.xslt2web.sys;

import edu.hziee.common.xslt2web.data.DbDataParameter;
import edu.hziee.common.xslt2web.data.TypeCode;

public class SQLParamBuilder implements IParamBuilder {
	private String sql;
	private DbDataParameter[] params;

	private SQLParamBuilder() {
		super();
	}

	public DbDataParameter[] getParams() {
		return params;
	}

	public String getSQL() {
		return sql;
	}

	public IParamBuilder createSingleSQL(TypeCode type, String fieldName,
			String oper, Object fieldValue) {
		sql = String.format("%s %s ?", fieldName, oper);
		DbDataParameter param = DbDataParameter.createParameter(type);
		param.setParameterName(fieldName);
		param.setValue(fieldValue);
		params = new DbDataParameter[] { param };
		return this;
	}

	public IParamBuilder createEqualSQL(TypeCode type, String fieldName,
			String fieldValue) {
		return createSingleSQL(type, fieldName, "=", fieldName);
	}

	/// <param name="fmtSQL">格式化字符串，要求SQL的名称与值必须使用连续的序号，名称使用0、2、4、6...
	/// 值使用1、3、5...。例如"%s = %s"，"(%s > %s AND %s &lt;= %s)"。不能使用诸如
	/// "%s BETWEEN %s AND %s"的格式</param>
	public IParamBuilder createCompositeSQL(String fmtSQL, TypeCode[] types,
			String[] fieldNames, Object... fieldValues) {
		Object[] fieldParams = new Object[fieldNames.length << 1]; // fieldNames.Length * 2
		params = new DbDataParameter[fieldNames.length];
		for (int i = 0; i < fieldNames.length; i++) {
			int index = i << 1;
			String fieldName = fieldNames[i];
			fieldParams[index] = fieldName;
			fieldParams[index + 1] = "?";

			DbDataParameter param = DbDataParameter.createParameter(types[i]);
			//param.DbType = DbType.AnsiString;
			param.setParameterName(fieldName);
			param.setValue(fieldValues[i]);
			params[i] = param;
		}
		sql = String.format(fmtSQL, fieldParams);
		return this;
	}

	public static IParamBuilder getSingleSQL(TypeCode type, String fieldName,
			String oper, Object fieldValue) {
		SQLParamBuilder builder = new SQLParamBuilder();
		return builder.createSingleSQL(type, fieldName, oper, fieldValue);
	}

	public static IParamBuilder getEqualSQL(TypeCode type, String fieldName,
			String fieldValue) {
		return getSingleSQL(type, fieldName, "=", fieldValue);
	}

	public static IParamBuilder getCompositeSQL(String fmtSQL,
			TypeCode[] types, String[] fieldNames, Object... fieldValues) {
		SQLParamBuilder builder = new SQLParamBuilder();
		return builder.createCompositeSQL(fmtSQL, types, fieldNames,
				fieldValues);
	}
}
