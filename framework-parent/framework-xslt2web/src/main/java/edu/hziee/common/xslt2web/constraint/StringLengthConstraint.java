package edu.hziee.common.xslt2web.constraint;

import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class StringLengthConstraint extends BaseConstraint {
	private final static String SCRIPT = "if (!CheckDataLength(\"%s\", \"%s\", \"%s\")) \n"
			+ "  return false;\n";

	private String message;
	private int length;

	public StringLengthConstraint(String fieldName, String displayName,
			int length) {
		super(fieldName, displayName);
		this.length = length;
		this.message = String.format("%s的长度不能大于%s", displayName, length);
	}

	@Override
	public boolean checkError(String value, int position) {
		if (StringUtil.isEmpty(value))
			return true;
		return StringUtil.getStringLength(value) <= length;
	}

	@Override
	public String getJavaScript() {
		return String.format(SCRIPT, getJSCtrlName(), message, length);
	}

	@Override
	public BaseErrorObject newErrorObject(int position) {
		return new BaseErrorObject(getFieldName(), message, position);
	}

}
