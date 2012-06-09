package edu.hziee.common.xslt2web.provider;

import edu.hziee.common.xslt2web.data.DbDataParameter;
import edu.hziee.common.xslt2web.data.TypeCode;
import edu.hziee.common.xslt2web.sys.IParamBuilder;

class CodeLikeParamBuilder implements IParamBuilder {
	private String sql;
	private DbDataParameter[] params;

	private CodeLikeParamBuilder() {
	}

	private IParamBuilder createLikeSQL(TypeCode type, String fieldName,
			String likeValue, String exceptValue) {
		sql = String.format("%1$s LIKE ? AND %1$s <> ?", fieldName);

		DbDataParameter param1 = DbDataParameter.createParameter(type);
		param1.setValue(likeValue);

		DbDataParameter param2 = DbDataParameter.createParameter(type);
		param2.setValue(exceptValue);

		params = new DbDataParameter[] { param1, param2 };

		return this;
	}

	public DbDataParameter[] getParams() {
		return params;
	}

	public String getSQL() {
		return sql;
	}

	public static IParamBuilder getLikeSQL(TypeCode type, String fieldName,
			String likeValue, String exceptValue) {
		CodeLikeParamBuilder builder = new CodeLikeParamBuilder();
		return builder.createLikeSQL(type, fieldName, likeValue, exceptValue);
	}
}
