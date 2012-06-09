package edu.hziee.common.xslt2web.constraint;

public class ChinaUnicomConstraint extends RegExConstraint {

	public ChinaUnicomConstraint(String fieldName, String displayName) {
		super(fieldName, displayName, "^1(3[0-3]|5[1-6])\\d{8}$");
		setMessage(String.format("%s�����й���ͨ�ĺ��룬����ȷ��д��", displayName));
	}

}
