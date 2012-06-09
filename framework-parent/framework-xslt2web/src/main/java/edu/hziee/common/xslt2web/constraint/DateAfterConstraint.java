package edu.hziee.common.xslt2web.constraint;

import java.text.ParseException;
import java.util.Date;

import edu.hziee.common.xslt2web.sysutil.DateUtil;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class DateAfterConstraint extends BaseConstraint {
	private String message;
	private String beforeFieldName;
	private String beforeDisplayName;

	public DateAfterConstraint(String fieldName, String displayName,
			String beforeFieldName, String beforeDisplayName) {
		super(fieldName, displayName);
		this.beforeFieldName = beforeFieldName;
		this.beforeDisplayName = beforeDisplayName;
		message = String.format("%s必须在%s之后！", this.beforeDisplayName, displayName);
	}

	@Override
	public boolean checkError(String value, int position) {
		String beforeValue = getDataSet().getTables().getItem(getTableName())
				.getRows().getItem(position).getItem(beforeFieldName)
				.toString();
		if (StringUtil.isEmpty(value) || StringUtil.isEmpty(beforeValue))
			return true;
		try {
			Date afterDate = DateUtil.parseDate(value);
			Date beforeDate = DateUtil.parseDate(beforeValue);
			return afterDate.compareTo(beforeDate) >= 0;
		} catch (ParseException ex) {
			return false;
		}
	}

	@Override
	public String getJavaScript() {
		return "";
	}

	@Override
	public BaseErrorObject newErrorObject(int position) {
		return new BaseErrorObject(getFieldName(), message, position);
	}

}
