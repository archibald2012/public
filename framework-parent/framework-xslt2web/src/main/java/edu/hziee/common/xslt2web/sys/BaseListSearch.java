package edu.hziee.common.xslt2web.sys;

import edu.hziee.common.xslt2web.data.DataRow;
import edu.hziee.common.xslt2web.data.TypeCode;

public abstract class BaseListSearch {
	private DataRow dataRow;
	private boolean isEqual;
	private SearchSQLType sqlType = SearchSQLType.Param;

	public BaseListSearch() {
	}

	public final SearchSQLType getSqlType() {
		return sqlType;
	}

	public final void setSqlType(SearchSQLType sqlType) {
		this.sqlType = sqlType;
	}

	public final DataRow getDataRow() {
		return dataRow;
	}

	public final boolean isEqual() {
		return isEqual;
	}

	public abstract IParamBuilder getParamCondition(TypeCode type,
			String fieldName, String fieldValue);

	public String getCondition(String fieldName, String fieldValue) {
		return "";
	}

	public void setProperties(DataRow row, boolean isEqual) {
		this.dataRow = row;
		this.isEqual = isEqual;
	}
}
