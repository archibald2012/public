package edu.hziee.common.xslt2web.provider;

import edu.hziee.common.xslt2web.data.TypeCode;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sys.IParamSearch;
import edu.hziee.common.xslt2web.sys.SearchSQLType;
import edu.hziee.common.xslt2web.sysutil.SqlUtil;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class ClassicParamSearch implements IParamSearch {
	public static final IParamSearch Search = new ClassicParamSearch();

	private ClassicParamSearch() {
	}

	private String getSql(String fieldName, String fieldValue, char wildChar) {
		boolean isChinese = false;
		char[] charArr = fieldValue.toCharArray();
		for (char c : charArr)
			if ((int) c > 255) {
				isChinese = true;
				break;
			}
		String[] fields = fieldName.split(",");
		String retCond = "";
		fieldValue = StringUtil.getSqlValue(fieldValue);
		for (String field : fields) {
			if (!StringUtil.isEmpty(retCond))
				retCond += " OR ";
			if (wildChar == '%' && SqlUtil.hasWideChar(fieldValue)) {
				fieldValue = StringUtil.replaceSQLChar(fieldValue);
				if (isChinese)
					retCond += String.format(
							"(%1$s LIKE '%%%2$s%%' ESCAPE '\\')", field,
							fieldValue);
				else
					retCond += String.format(
							"((%1$s LIKE '%2$s%%' ESCAPE '\\') OR (%%1$s LIKE '%3$s%%' ESCAPE '\\')"
									+ "OR (%4$s))", field, fieldValue
									.toUpperCase(), fieldValue.toLowerCase(),
							SqlUtil.getCharFullCondition(field, fieldValue));
			} else
				retCond += (isChinese) ? String.format(
						"(%1$s LIKE '%3$s%2$%3$s')", field, fieldValue,
						wildChar)
						: String
								.format(
										"((%1$s LIKE '%2$s%5$s') OR (%1$s LIKE '%3$s%5$s') OR (%4$s))",
										field, fieldValue.toUpperCase(),
										fieldValue.toLowerCase(), SqlUtil
												.getCharFullCondition(field,
														fieldValue), wildChar);
		}
		return retCond;
	}

	public String getCondition(String fieldName, String fieldValue) {
		return getSql(fieldName, fieldValue, '%');
	}

	public IParamBuilder getParamCondition(TypeCode type, String fieldName,
			String fieldValue) {
		return null;
	}

	public SearchSQLType getType() {
		return SearchSQLType.String;
	}

}
