package edu.hziee.common.xslt2web.provider;

import edu.hziee.common.xslt2web.data.TypeCode;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sys.IParamSearch;
import edu.hziee.common.xslt2web.sys.SearchSQLType;
import edu.hziee.common.xslt2web.sysutil.SqlUtil;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class ClassicPYParamSearch implements IParamSearch {
	public static final IParamSearch Search = new ClassicPYParamSearch();

	private ClassicPYParamSearch() {
	}

	public String getCondition(String fieldName, String fieldValue) {
		fieldValue = StringUtil.getSqlValue(fieldValue);
		if (SqlUtil.hasWideChar(fieldValue)) {
			fieldValue = StringUtil.replaceSQLChar(fieldValue);
			return String
					.format(
							"((%1$s LIKE '%2$s%%' ESCAPE '\\') OR (%1$s LIKE '%3$s%%' ESCAPE '\\') OR (%4$s))",
							fieldName, fieldValue.toUpperCase(), fieldValue
									.toLowerCase(),
							SqlUtil.getCharFullCondition(fieldName, fieldValue));
		} else
			return String.format(
					"((%1$s LIKE '%2$s%%') OR (%1$s LIKE '%3$s%%') OR (%4$s))",
					fieldName, fieldValue.toUpperCase(), fieldValue
							.toLowerCase(), SqlUtil.getCharFullCondition(
							fieldName, fieldValue));
	}

	public IParamBuilder getParamCondition(TypeCode type, String fieldName,
			String fieldValue) {
		return null;
	}

	public SearchSQLType getType() {
		return SearchSQLType.String;
	}

}
