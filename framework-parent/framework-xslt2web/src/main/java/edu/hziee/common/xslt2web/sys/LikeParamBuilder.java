package edu.hziee.common.xslt2web.sys;

import edu.hziee.common.xslt2web.data.DbDataParameter;
import edu.hziee.common.xslt2web.data.TypeCode;

public class LikeParamBuilder implements IParamBuilder {
	private String sql;
	private DbDataParameter[] params;
	private boolean isEscape;

	private LikeParamBuilder() {
		super();
	}

	public DbDataParameter[] getParams() {
		return params;
	}

	public String getSQL() {
		return sql;
	}

	public IParamBuilder createLikeSQL(TypeCode type, String fieldName,
			String fieldValue) {
		isEscape = fieldValue.indexOf('%') != -1
				|| fieldValue.indexOf('_') != -1;
		String fieldName1 = fieldName + "1";
		String fieldName2 = fieldName + "2";

		if (isEscape) {
			sql = String.format(
					"((%s LIKE ? ESCAPE '\\') OR (%s LIKE ? ESCAPE '\\'))",
					fieldName, fieldName);
			// fieldValue = StringUtil.ReplaceSQLChar(fieldValue);
		} else
			sql = String.format("((%s LIKE ?) OR (%s LIKE ?))", fieldName,
					fieldName);

		DbDataParameter param1 = DbDataParameter.createParameter(type);
		// param1.DbType = DbType.AnsiString;
		param1.setParameterName(fieldName1);
		param1.setValue(fieldValue + "%");

		DbDataParameter param2 = DbDataParameter.createParameter(type);
		// param2.DbType = DbType.AnsiString;
		param1.setParameterName(fieldName2);
		param1.setValue("%" + fieldValue + "%");

		params = new DbDataParameter[] { param1, param2 };

		return this;
	}

	public static IParamBuilder getLikeSQL(TypeCode type, String fieldName,
			String fieldValue) {
		LikeParamBuilder builder = new LikeParamBuilder();
		return builder.createLikeSQL(type, fieldName, fieldValue);
	}
}
