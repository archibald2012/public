package edu.hziee.common.xslt2web.constraint;

public class MobileConstraint extends RegExConstraint {

	public MobileConstraint(String fieldName, String displayName) {
		super(fieldName, displayName, "^1(3[0-9]|5[0-9])\\d{8}$");
		setMessage(String.format("%s�����ֻ����룬����ȷ��д��", displayName));
	}

}
