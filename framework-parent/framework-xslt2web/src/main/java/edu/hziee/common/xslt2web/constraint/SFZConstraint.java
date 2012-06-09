package edu.hziee.common.xslt2web.constraint;

public class SFZConstraint extends RegExConstraint {

	public SFZConstraint(String fieldName, String displayName) {
		super(fieldName, displayName, "^\\d{15}$|^\\d{17}[0-9x]$");
		setMessage(String.format("%s不是身份证号码，请正确填写！", displayName));
	}
}
