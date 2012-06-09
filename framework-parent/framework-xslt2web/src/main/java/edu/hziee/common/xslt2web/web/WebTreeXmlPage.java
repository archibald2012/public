package edu.hziee.common.xslt2web.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import yjc.toolkit.data.TreeHelper;
import yjc.toolkit.data.XmlDataSetProvider;
import yjc.toolkit.sys.PageStyle;
import yjc.toolkit.sysutil.StringUtil;

public class WebTreeXmlPage extends WebXmlHttpPage {
	public WebTreeXmlPage(HttpServlet servlet, HttpServletRequest request,
			HttpServletResponse response) {
		super(servlet, request, response);
		setStyle(PageStyle.Custom);
		String operation = getRequest().getParameter("Oper");
		if (StringUtil.isEmpty(operation))
			operation = TreeHelper.DEFAULT_OPER;
		setOperation(operation);

		setModule(true);
		setDataProvider(XmlDataSetProvider.Instance);
		//SupportLogin = fModule.GetPageSupportLogin(Style, Operation);
	}

	@Override
	protected void writePage() {
		if (TreeHelper.SUB_TREE_OPER.equals(getOperation())) {
			String s = getXmlString();
			getResponse().setContentType("text/xml");
			getResponseWriter().write(s);
		} else
			super.writePage();
	}

}
