package edu.hziee.common.xslt2web.constraint;

public class ChinaUnicomConstraint extends RegExConstraint {

	public ChinaUnicomConstraint(String fieldName, String displayName) {
		super(fieldName, displayName, "^1(3[0-3]|5[1-6])\\d{8}$");
		setMessage(String.format("%s不是中国联通的号码，请正确填写！", displayName));
	}

}
