package edu.hziee.common.xslt2web.constraint;

public class PostCodeConstraint extends RegExConstraint {
	public PostCodeConstraint(String fieldName, String displayName) {
		super(fieldName, displayName, "^\\d{6}$");
		setMessage(String.format("%s不是邮政编码，请正确填写！", displayName));
	}
}
