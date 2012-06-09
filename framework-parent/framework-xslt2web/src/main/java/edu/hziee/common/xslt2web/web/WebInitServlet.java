package edu.hziee.common.xslt2web.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebInitServlet extends WebBaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WebInitServlet() {
		super();
	}

	@Override
	protected WebBasePage createPage(HttpServletRequest request,
			HttpServletResponse response) {
		String source = request.getParameter("Source");
		WebPageRegCategory categroy = (WebPageRegCategory) (getAppGbl()
				.getRegsCollection().get(WebPageRegCategory.REG_NAME));
		WebBasePage page = categroy.newArgsInstance(source, this, request,
				response);
		return page;
	}
}
