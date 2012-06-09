package edu.hziee.common.xslt2web.provider;

import java.text.ParseException;
import java.util.Date;

import edu.hziee.common.xslt2web.data.DbDataParameter;
import edu.hziee.common.xslt2web.data.TypeCode;
import edu.hziee.common.xslt2web.sys.BaseListSearch;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sysutil.DateUtil;

public class SimpleDateListSearch extends BaseListSearch {
	private class DateEqualParamBuilder implements IParamBuilder {
		private String sql;

		private DbDataParameter[] params;

		public DateEqualParamBuilder(TypeCode type, String fieldName,
				String fieldValue) {
			try {
				Date date = DateUtil.parseDate(fieldValue);
				String fieldName1 = fieldName + "1";
				String fieldName2 = fieldName + "2";
				sql = String.format("%s >= ? AND %s < ?", fieldName, fieldName);

				DbDataParameter param1 = DbDataParameter.createParameter(type);
				param1.setParameterName(fieldName1);
				param1.setValue(date);

				DbDataParameter param2 = DbDataParameter.createParameter(type);
				param1.setParameterName(fieldName2);
				param1.setValue(DateUtil.addDays(date, 1));

				params = new DbDataParameter[] { param1, param2 };
			} catch (ParseException ex) {
				sql = "";
				params = new DbDataParameter[0];
			}
		}

		public DbDataParameter[] getParams() {
			return params;
		}

		public String getSQL() {
			return sql;
		}

	}

	public static final BaseListSearch Search = new SimpleDateListSearch();

	private SimpleDateListSearch() {
	}

	@Override
	public IParamBuilder getParamCondition(TypeCode type, String fieldName,
			String fieldValue) {
		return new DateEqualParamBuilder(type, fieldName, fieldValue);
	}

}
