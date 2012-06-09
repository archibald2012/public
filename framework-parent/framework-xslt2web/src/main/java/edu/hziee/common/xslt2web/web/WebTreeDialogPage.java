package edu.hziee.common.xslt2web.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import yjc.toolkit.easysearch.EasySearchTreeDataSet;
import yjc.toolkit.sys.PageStyle;

@WebPageAnnotation(regName = WebTreeDialogPage.REG_NAME, author = "YJC", createDate = "2008-11-02", description = "ΪEasySearch��Treeģ���ṩXml���")
public class WebTreeDialogPage extends WebDataSetPage {

	public final static String REG_NAME = "TreeDialogPage";

	public WebTreeDialogPage(HttpServlet servlet, HttpServletRequest request,
			HttpServletResponse response) {
		super(servlet, request, response);

		setStyle(PageStyle.Custom);
		setSource(new EasySearchTreeDataSet());
	}

	@Override
	protected void doGet() {
		super.doGet();
		
		writePage();
	}

	private void writePage() {
		String s = getSource().getXmlString();
		getResponse().setContentType("text/xml");
		getResponse().setCharacterEncoding("UTF8");
		getResponseWriter().write(s);
	}
}
