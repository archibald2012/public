package edu.hziee.common.xslt2web.constraint;

import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class IntRangeConstraint extends BaseConstraint {
	private final static String SCRIPT = "if (!CheckNumericInRange(\"%s\", \"%s\", %s, %s)) \n"
			+ "  return false;\n";

	private String message;
	private int low;
	private int high;

	public IntRangeConstraint(String fieldName, String displayName, int low,
			int high) {
		super(fieldName, displayName);
		this.low = low;
		this.high = high;
		setErrorMsg();
	}

	private void setErrorMsg() {
		if (low == Integer.MIN_VALUE && high == Integer.MAX_VALUE)
			message = String.format("%s的值应该在%s和%s之间", getDisplayName(), low,
					high);
		else if (low == Integer.MIN_VALUE)
			message = String.format("%s的值应该小于%s", getDisplayName(), high);
		else if (high == Integer.MAX_VALUE)
			message = String.format("%s的值应该大于%s", getDisplayName(), low);
		else
			message = String.format("%s的值应该在%s和%s之间", getDisplayName(), low,
					high);
	}

	@Override
	public boolean checkError(String value, int position) {
		if (StringUtil.isEmpty(value))
			return true;
		int temp = Integer.parseInt(value);
		return (temp >= low && temp <= high);
	}

	@Override
	public String getJavaScript() {
		return String.format(SCRIPT, getJSCtrlName(), message, low, high);
	}

	@Override
	public BaseErrorObject newErrorObject(int position) {
		return new BaseErrorObject(getFieldName(), message, position);

	}

}
