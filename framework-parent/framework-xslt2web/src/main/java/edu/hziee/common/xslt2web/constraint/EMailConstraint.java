package edu.hziee.common.xslt2web.constraint;

import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class EMailConstraint extends BaseConstraint {
	private final static String SCRIPT = "if (!CheckEmail(\"%s\", \"%s!\")) \n"
			+ "  return false;\n";
	private String message;

	public EMailConstraint(String fieldName, String displayName) {
		super(fieldName, displayName);
		message = String.format("%s不是EMail地址，请正确填写", displayName);
	}

	@Override
	public boolean checkError(String value, int position) {
		if (StringUtil.isEmpty(value))
			return true;
		return !(value.indexOf("@") == -1);
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
