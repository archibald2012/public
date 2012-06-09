package edu.hziee.common.xslt2web.provider;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import edu.hziee.common.xslt2web.data.TypeCode;
import edu.hziee.common.xslt2web.sys.BaseListSearch;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sysutil.DateUtil;

public final class DateSpanListSearch {
	public static BaseListSearch SmallSearch = new DateSmallListSearch();

	public static BaseListSearch BigSearch = new DateBigListSearch();

	public DateSpanListSearch() {
	}

	public static void add(HashMap<String, BaseListSearch> searches,
			String fieldName) {
		searches.put(fieldName, SmallSearch);
		searches.put(fieldName + "END", BigSearch);
	}
}

class DateBigListSearch extends BaseListSearch {
	@Override
	public IParamBuilder getParamCondition(TypeCode type, String fieldName,
			String fieldValue) {
		try {
			Date date = DateUtil.parseDate(fieldValue);
			fieldName = fieldName.substring(0, fieldName.length() - 3);
			return new InternalParamSearch(type, fieldName, "<", DateUtil
					.addDays(date, 1));
		} catch (ParseException e) {
			return new InternalParamSearch("1 = 0");
		}
	}
}

class DateSmallListSearch extends BaseListSearch {
	@Override
	public IParamBuilder getParamCondition(TypeCode type, String fieldName,
			String fieldValue) {
		try {
			Date date = DateUtil.parseDate(fieldValue);
			return new InternalParamSearch(type, fieldName, ">=", date);
		} catch (ParseException e) {
			return new InternalParamSearch("1 = 0");
		}
	}
}
