package edu.hziee.common.xslt2web.constraint;

import java.text.ParseException;

import edu.hziee.common.xslt2web.sysutil.DateUtil;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class DateConstraint extends BaseConstraint {
	private final static String SCRIPT = "if (!CheckDate(\"%s\", \"%s\")) \n"
			+ "  return false;\n";
	private String message;

	public DateConstraint(String fieldName, String displayName) {
		super(fieldName, displayName);
		setFirstCheck(true);
		message = String.format("%s必须是日期类型", displayName);
	}

	@Override
	public boolean checkError(String value, int position) {
		if (StringUtil.isEmpty(value))
			return true;
		try {
			DateUtil.parseDate(value);
			return true;
		} catch (ParseException ex) {
			return false;
		}
	}

	@Override
	public String getJavaScript() {
		return String.format(SCRIPT, getJSCtrlName(), message);
	}

	@Override
	public BaseErrorObject newErrorObject(int position) {
		return new BaseErrorObject(getFieldName(), message, position);
	}
}
