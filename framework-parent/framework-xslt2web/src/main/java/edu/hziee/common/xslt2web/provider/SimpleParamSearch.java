package edu.hziee.common.xslt2web.provider;

import edu.hziee.common.xslt2web.data.TypeCode;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sys.IParamSearch;
import edu.hziee.common.xslt2web.sys.SQLParamBuilder;
import edu.hziee.common.xslt2web.sys.SearchSQLType;

public class SimpleParamSearch implements IParamSearch {
	public static final IParamSearch Search = new LikeParamSearch();

	public SimpleParamSearch() {
	}

	public String getCondition(String fieldName, String fieldValue) {
		return null;
	}

	public IParamBuilder getParamCondition(TypeCode type, String fieldName,
			String fieldValue) {
		return SQLParamBuilder.getSingleSQL(type, fieldName, "LIKE", String
				.format("%%%s%%", fieldValue));
	}

	public SearchSQLType getType() {
		return SearchSQLType.Param;
	}

}
