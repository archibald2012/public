package edu.hziee.common.xslt2web.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import yjc.toolkit.data.BaseDataSet;
import yjc.toolkit.data.UpdateKind;
import yjc.toolkit.exception.ToolkitException;
import yjc.toolkit.sys.IInsertPage;
import yjc.toolkit.sys.PageStyle;

public class WebInsertPage extends WebXmlHttpPage {
	private IInsertPage insertPage;

	public WebInsertPage(HttpServlet servlet, HttpServletRequest request,
			HttpServletResponse response) {
		super(servlet, request, response);
		setStyle(PageStyle.Insert);
		setSubFunctionKey(INSERT_SUB_KEY);
		setStatus(UpdateKind.Insert);
		setSupportCheckSubmit(true);
	}

	@Override
	public void setSource(BaseDataSet source) {
		// TODO �Զ���ɷ������
		super.setSource(source);
		if (source != null) {
			if (source instanceof IInsertPage)
				insertPage = (IInsertPage) source;
			else
				throw new ToolkitException(
						"��ȻҪ��Update��DataSet��֧��IInsertPage��ô���ԣ�");
		}
	}

	public final IInsertPage getInsertPage() {
		return insertPage;
	}

}
