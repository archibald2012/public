package edu.hziee.common.xslt2web.provider;

import java.util.HashMap;

import edu.hziee.common.xslt2web.data.DbDataParameter;
import edu.hziee.common.xslt2web.data.TypeCode;
import edu.hziee.common.xslt2web.sys.BaseListSearch;
import edu.hziee.common.xslt2web.sys.IParamBuilder;

public final class SpanListSearch {
	public static BaseListSearch SmallSearch = new SmallListSearch();

	public static BaseListSearch BigSearch = new BigListSearch();

	private SpanListSearch() {
	}

	public static void add(HashMap<String, BaseListSearch> searches,
			String fieldName) {
		searches.put(fieldName, SmallSearch);
		searches.put(fieldName + "END", BigSearch);
	}

}

class BigListSearch extends BaseListSearch {
	@Override
	public IParamBuilder getParamCondition(TypeCode type, String fieldName,
			String fieldValue) {
		fieldName = fieldName.substring(0, fieldName.length() - 3);
		return new InternalParamSearch(type, fieldName, "<=", fieldValue);
	}
}

class SmallListSearch extends BaseListSearch {
	@Override
	public IParamBuilder getParamCondition(TypeCode type, String fieldName,
			String fieldValue) {
		return new InternalParamSearch(type, fieldName, ">=", fieldValue);
	}
}

class InternalParamSearch implements IParamBuilder {
	private String sql;

	private DbDataParameter[] params;

	public InternalParamSearch(TypeCode type, String fieldName, String oper,
			Object fieldValue) {
		sql = String.format("%s %s ?", fieldName, oper);
		DbDataParameter param = DbDataParameter.createParameter(type);
		param.setValue(fieldValue);
		params = new DbDataParameter[] { param };
	}
	
	public InternalParamSearch(String sql) 
	{
		this.sql = sql;
		params = new DbDataParameter[0];
	}

	public DbDataParameter[] getParams() {
		return params;
	}

	public String getSQL() {
		return sql;
	}
}
