package edu.hziee.common.xslt2web.constraint;

import java.text.ParseException;

import edu.hziee.common.xslt2web.sysutil.DateUtil;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class CurrentDateConstraint extends BaseConstraint {
	private String message;

	public CurrentDateConstraint(String fieldName, String displayName) {
		super(fieldName, displayName);
		message = String.format("%s不能大于当前日期！", displayName);
	}

	@Override
	public boolean checkError(String value, int position) {
		if (StringUtil.isEmpty(value))
			return true;
		try {
			return DateUtil.getCurrentDate().compareTo(
					DateUtil.parseDate(value)) > 0;
		} catch (ParseException e) {
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
