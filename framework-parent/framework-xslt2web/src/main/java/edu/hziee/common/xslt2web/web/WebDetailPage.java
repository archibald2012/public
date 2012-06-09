package edu.hziee.common.xslt2web.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import yjc.toolkit.data.BaseDataSet;
import yjc.toolkit.exception.ToolkitException;
import yjc.toolkit.sys.IDetailPage;
import yjc.toolkit.sys.PageStyle;

public class WebDetailPage extends WebXmlPage {
	private IDetailPage detailPage;

	public WebDetailPage(HttpServlet servlet, HttpServletRequest request,
			HttpServletResponse response) {
		super(servlet, request, response);
		setStyle(PageStyle.Detail);
		setSubFunctionKey(DETAIL_SUB_KEY);
	}

	@Override
	public void setSource(BaseDataSet source) {
		super.setSource(source);
		if (source != null) {
			if (source instanceof IDetailPage)
				detailPage = (IDetailPage) source;
			else
				throw new ToolkitException(
						"��ȻҪ��Update��DataSet��֧��IDetail2Page��ô���ԣ�");
		}
	}

	public final IDetailPage getDetailPage() {
		return detailPage;
	}

}
