package edu.hziee.common.xslt2web.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import yjc.toolkit.sys.AppSetting;

public class EncodingFilter implements Filter {

	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		AppSetting setting = AppSetting.getCurrent();
		if (setting != null) {
			request.setCharacterEncoding(setting.getRequestEncoding());
			String method = ((HttpServletRequest)request).getMethod();
			if ("GET".equalsIgnoreCase(method))
				response.setCharacterEncoding(setting.getResponseGetEncoding());
			else
				response.setCharacterEncoding(setting.getResponsePostEncoding());
		}

		chain.doFilter(request, response);
	}

	public void init(FilterConfig config) throws ServletException {
	}

}
