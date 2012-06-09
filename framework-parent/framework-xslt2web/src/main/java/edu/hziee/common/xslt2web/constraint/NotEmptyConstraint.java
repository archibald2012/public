package edu.hziee.common.xslt2web.constraint;

import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class NotEmptyConstraint extends BaseConstraint {
	private final static String SCRIPT = "if (!CheckEmpty(\"%s\", \"%s\")) \n "
			+ "  return false;\n";

	private String message;

	public NotEmptyConstraint(String fieldName, String displayName) {
		super(fieldName, displayName);
		setFirstCheck(true);
		message = String.format("%s²»ÄÜÎª¿Õ£¡", displayName);
	}

	@Override
	public boolean checkError(String value, int position) {
		return !StringUtil.isEmpty(value);
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
