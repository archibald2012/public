package edu.hziee.common.xslt2web.provider;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import edu.hziee.common.xslt2web.data.TypeCode;
import edu.hziee.common.xslt2web.sys.BaseListSearch;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sysutil.DateUtil;

public final class DateTimeSpanListSearch {
	public static BaseListSearch SmallSearch = new DateTimeSmallListSearch();

	public static BaseListSearch BigSearch = new DateTimeBigListSearch();

	public DateTimeSpanListSearch() {
	}

	public static void add(HashMap<String, BaseListSearch> searches,
			String fieldName) {
		searches.put(fieldName, SmallSearch);
		searches.put(fieldName + "END", BigSearch);
	}
}

class DateTimeBigListSearch extends BaseListSearch {
	@Override
	public IParamBuilder getParamCondition(TypeCode type, String fieldName,
			String fieldValue) {
		try {
			Date date = DateUtil.parseDateTime(fieldValue);
			fieldName = fieldName.substring(0, fieldName.length() - 3);
			return new InternalParamSearch(type, fieldName, "<=", date);
		} catch (ParseException e) {
			return new InternalParamSearch("1 = 0");
		}
	}
}

class DateTimeSmallListSearch extends BaseListSearch {
	@Override
	public IParamBuilder getParamCondition(TypeCode type, String fieldName,
			String fieldValue) {
		try {
			Date date = DateUtil.parseDateTime(fieldValue);
			return new InternalParamSearch(type, fieldName, ">=", date);
		} catch (ParseException e) {
			return new InternalParamSearch("1 = 0");
		}
	}
}