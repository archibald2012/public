package edu.hziee.common.xslt2web.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebEasySearchServlet extends WebBaseServlet {
	private static final long serialVersionUID = -1L;

	@Override
	protected WebBasePage createPage(HttpServletRequest request,
			HttpServletResponse response) {
		return new WebEasySearchPage(this, request, response);
	}

}
