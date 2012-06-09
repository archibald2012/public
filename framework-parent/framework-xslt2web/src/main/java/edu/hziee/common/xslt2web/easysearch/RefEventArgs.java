package edu.hziee.common.xslt2web.easysearch;

import edu.hziee.common.xslt2web.data.TypeCode;
import edu.hziee.common.xslt2web.sys.EventArgs;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sys.SQLParamBuilder;
import edu.hziee.common.xslt2web.sys.SearchSQLType;

public class RefEventArgs extends EventArgs {
	private static final long serialVersionUID = 1L;

	private String fieldName;

	private String refValue;

	private String sql;

	private SearchSQLType type;

	private IParamBuilder builder;

	public RefEventArgs(Object source) {
		super(source);
	}

	final void setProperties(TypeCode type, String fieldName, String refValue) {
		this.fieldName = fieldName;
		this.refValue = refValue;
		this.type = SearchSQLType.Param;
		this.sql = "";
		builder = SQLParamBuilder.getEqualSQL(type, fieldName, refValue);
	}

	public final IParamBuilder getBuilder() {
		return builder;
	}

	public final void setBuilder(IParamBuilder builder) {
		this.builder = builder;
	}

	public final String getSQL() {
		return sql;
	}

	public final void setSQL(String sql) {
		this.sql = sql;
	}

	public final SearchSQLType getType() {
		return type;
	}

	public final void setType(SearchSQLType type) {
		this.type = type;
	}

	public final String getFieldName() {
		return fieldName;
	}

	public final String getRefValue() {
		return refValue;
	}
}
