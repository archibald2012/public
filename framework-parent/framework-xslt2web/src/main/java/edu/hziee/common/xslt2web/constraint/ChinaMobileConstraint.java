package edu.hziee.common.xslt2web.constraint;

public class ChinaMobileConstraint extends RegExConstraint {

	public ChinaMobileConstraint(String fieldName, String displayName) {
		super(fieldName, displayName, "^1(3[4-9]|5[0,7-9])\\d{8}$");
		setMessage(String.format("%s�����й��ƶ��ĺ��룬����ȷ��д��", displayName));
	}

}
