package edu.hziee.common.xslt2web.constraint;

public class ChinaMobileConstraint extends RegExConstraint {

	public ChinaMobileConstraint(String fieldName, String displayName) {
		super(fieldName, displayName, "^1(3[4-9]|5[0,7-9])\\d{8}$");
		setMessage(String.format("%s不是中国移动的号码，请正确填写！", displayName));
	}

}
