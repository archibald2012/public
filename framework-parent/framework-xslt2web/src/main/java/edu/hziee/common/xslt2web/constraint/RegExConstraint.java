package edu.hziee.common.xslt2web.constraint;

import java.util.regex.Pattern;

import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class RegExConstraint extends BaseConstraint {
	private Pattern regex;
	private String message;

	public RegExConstraint(String fieldName, String displayName, String pattern) {
		this(fieldName, displayName, pattern, 0);
	}

	public RegExConstraint(String fieldName, String displayName,
			String pattern, int options) {
		super(fieldName, displayName);

		regex = Pattern.compile(pattern, options);
		message = String.format("%s不符合正则表达式\"%s\"", displayName, pattern);
	}

	public final String getMessage() {
		return message;
	}

	public final void setMessage(String message) {
		this.message = message;
	}

	public final Pattern getRegex() {
		return regex;
	}

	@Override
	public boolean checkError(String value, int position) {
		if (StringUtil.isEmpty(value))
			return true;
		return regex.matcher(value).matches();
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
