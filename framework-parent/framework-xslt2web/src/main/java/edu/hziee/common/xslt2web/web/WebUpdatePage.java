package edu.hziee.common.xslt2web.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import yjc.toolkit.data.BaseDataSet;
import yjc.toolkit.data.UpdateKind;
import yjc.toolkit.exception.ToolkitException;
import yjc.toolkit.sys.IUpdatePage;
import yjc.toolkit.sys.PageStyle;

public class WebUpdatePage extends WebXmlHttpPage {
	private IUpdatePage updatePage;

	public WebUpdatePage(HttpServlet servlet, HttpServletRequest request,
			HttpServletResponse response) {
		super(servlet, request, response);
		
		setStyle(PageStyle.Update);
		setSubFunctionKey(UPDATE_SUB_KEY);
		setStatus(UpdateKind.Update);
		setSupportCheckSubmit(true);
	}

	@Override
	public void setSource(BaseDataSet source) {
		super.setSource(source);
		
		if (source != null) {
			if (source instanceof IUpdatePage)
				updatePage = (IUpdatePage) source;
			else
				throw new ToolkitException(
						"��ȻҪ��Update��DataSet��֧��IUpdatePagee��ô���ԣ�");
		}
	}

	public final IUpdatePage getUpdatePage() {
		return updatePage;
	}

}
