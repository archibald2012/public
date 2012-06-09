package edu.hziee.common.xslt2web.sys;

import edu.hziee.common.xslt2web.data.TypeCode;

public interface IParamSearch {
	SearchSQLType getType();

	IParamBuilder getParamCondition(TypeCode type, String fieldName,
			String fieldValue);

	String getCondition(String fieldName, String fieldValue);
}
