package edu.hziee.common.xslt2web.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import yjc.toolkit.data.XmlDataSetProvider;

public class WebListXmlPage extends WebListPage {

	public WebListXmlPage(HttpServlet servlet, HttpServletRequest request,
			HttpServletResponse response) {
		super(servlet, request, response);
		setModule(true);
		setDataProvider(XmlDataSetProvider.Instance);
	}

}
