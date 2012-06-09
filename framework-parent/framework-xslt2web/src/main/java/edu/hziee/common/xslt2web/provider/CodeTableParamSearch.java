package edu.hziee.common.xslt2web.provider;

import edu.hziee.common.xslt2web.data.TypeCode;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sys.IParamSearch;
import edu.hziee.common.xslt2web.sys.SQLParamBuilder;
import edu.hziee.common.xslt2web.sys.SearchSQLType;

public class CodeTableParamSearch implements IParamSearch {
	public static final IParamSearch Search = new CodeTableParamSearch();

	private CodeTableParamSearch() {
	}

	public String getCondition(String fieldName, String fieldValue) {
		return "";
	}

	public IParamBuilder getParamCondition(TypeCode type, String fieldName,
			String fieldValue) {
		boolean isChinese = false, isPy = true;
		char[] charArr = fieldValue.toCharArray();
		for (char c : charArr) {
			if ((int) c > 255) {
				isChinese = true;
				break;
			}
			char lower = Character.toLowerCase(c);
			if (lower < 'a' || lower > 'z') {
				isPy = false;
				break;
			}
		}
		if (isChinese)
			return SQLParamBuilder.getSingleSQL(type, "CODE_NAME", "LIKE",
					String.format("%%%s%%", fieldValue));
		if (isPy)
			return SQLParamBuilder.getSingleSQL(type, "CODE_PY", "LIKE",
					fieldValue.toUpperCase() + "%");
		return SQLParamBuilder.getSingleSQL(type, "CODE_VALUE", "LIKE",
				fieldValue + "%");
	}

	public SearchSQLType getType() {
		return SearchSQLType.Param;
	}

}
