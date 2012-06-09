package edu.hziee.common.xslt2web.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import yjc.toolkit.sys.AppSetting;
import yjc.toolkit.sys.ISessionStart;
import yjc.toolkit.sys.SessionGlobal;
import yjc.toolkit.sysutil.ClassUtil;
import yjc.toolkit.xml.SingleDoubleTransform;

public final class SessionFilter implements Filter {
	private static final String HEADER_XSLT = "Global\\Header.xslt";
	private static final String BOTTOM_XSLT = "Global\\Bottom.xslt";
	private static final String CONTENT_XSLT = "Global\\HeadLeft.xslt";
	private static final String ERROR_XSLT = "Global\\Wrong.xslt";

	public void destroy() {
	}

	private synchronized void initialize(ServletRequest request,
			ServletResponse response, HttpSession session) {
		Object obj = session.getAttribute(SessionGlobal.SESSION_CONST);
		if (obj == null) {
			SessionGlobal global = new SessionGlobal();
			global.getIeFiles().setHeader(HEADER_XSLT);
			global.getNavFiles().setHeader(HEADER_XSLT);
			global.getIeFiles().setBottom(BOTTOM_XSLT);
			global.getNavFiles().setBottom(BOTTOM_XSLT);
			global.getIeFiles().setContent(CONTENT_XSLT);
			global.getNavFiles().setContent(CONTENT_XSLT);
			global.getIeFiles().setError(ERROR_XSLT);
			global.getNavFiles().setError(ERROR_XSLT);
			// set transform
			global.setDoubleTransform(new SingleDoubleTransform());
			
			// start
			Class<ISessionStart> startClass = ClassUtil.findClass(AppSetting
					.getCurrent().getSessionStartClass());
			ISessionStart start = ClassUtil.createObject(startClass);
			try {
				start.start(request, response, global);
			} catch (Exception ex) {

			}

			session.setAttribute(SessionGlobal.SESSION_CONST, global);
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpSession session = ((HttpServletRequest) request).getSession();
		Object obj = session.getAttribute(SessionGlobal.SESSION_CONST);
		if (obj == null) {
			initialize(request, response, session);
		}

		chain.doFilter(request, response);
	}

	public void init(FilterConfig config) throws ServletException {
	}
}
