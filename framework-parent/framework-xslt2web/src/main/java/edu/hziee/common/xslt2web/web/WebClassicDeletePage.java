package edu.hziee.common.xslt2web.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import yjc.toolkit.data.DataSetProvider;

public class WebClassicDeletePage extends WebDeletePage {

	public WebClassicDeletePage(HttpServlet servlet,
			HttpServletRequest request, HttpServletResponse response) {
		super(servlet, request, response);
		setDataProvider(DataSetProvider.Instance);
		setSupportLogin(true);
	}

}
