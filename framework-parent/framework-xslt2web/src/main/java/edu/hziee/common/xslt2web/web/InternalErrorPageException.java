package edu.hziee.common.xslt2web.web;

import yjc.toolkit.exception.ErrorPageException;

class InternalErrorPageException extends ErrorPageException {
	private static final long serialVersionUID = 1L;

	public InternalErrorPageException(int errorID) {
		String title = String.format("�ڲ�����--%d", errorID);
		setErrorTitle(title);
		setPageTitle(title);
		String body = String.format("�������кţ�%d�����¼�����кţ�����ϵͳ����Ա��ϵ��", errorID);
		setErrorBody(body);
	}
}
