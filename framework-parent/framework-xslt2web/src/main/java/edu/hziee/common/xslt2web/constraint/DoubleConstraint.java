package edu.hziee.common.xslt2web.constraint;

import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class DoubleConstraint extends BaseConstraint {

	private final static String SCRIPT = "if (!CheckNumeric(\"%s\", \"%s\")) \n"
			+ "  return false;\n";

	private String message;

	public DoubleConstraint(String fieldName, String displayName) {
		super(fieldName, displayName);
		setFirstCheck(true);
		message = String.format("%s必须是Double类型", displayName);
	}

	@Override
	public boolean checkError(String value, int position) {
		if (StringUtil.isEmpty(value))
			return true;
		try {
			Double.parseDouble(value);
			return true;
		} catch (Exception ex) {
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
