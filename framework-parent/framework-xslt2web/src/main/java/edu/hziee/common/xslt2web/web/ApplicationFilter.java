package edu.hziee.common.xslt2web.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import edu.hziee.common.xslt2web.configxml.AppConfigXml;
import edu.hziee.common.xslt2web.data.ContextConnectionCreator;
import edu.hziee.common.xslt2web.provider.GlobalProvider;
import edu.hziee.common.xslt2web.sys.AppSetting;
import edu.hziee.common.xslt2web.sys.ApplicationGlobal;
import edu.hziee.common.xslt2web.sys.IApplicationStart;

public final class ApplicationFilter implements Filter {
	private FilterConfig config;

	public void destroy() {

	}

	private synchronized void initialize(ServletRequest request,
			ServletResponse response) {
		ServletContext context = config.getServletContext();
		Object obj = context.getAttribute(ApplicationGlobal.APP_CONST);
		if (obj == null) {
			String file = context.getInitParameter("applicationXml");
			AppConfigXml xml = new AppConfigXml();
			xml.loadFile(file);
			AppSetting setting = new AppSetting(xml);
			AppSetting.setCurrent(setting);
			ApplicationGlobal global = new ApplicationGlobal();
			Class<IApplicationStart> startClass = ClassUtil.findClass(setting
					.getAppStartClass());
			IApplicationStart start = ClassUtil.createObject(startClass);
			try {
				start.start(request, response, setting, global);
			} catch (Exception ex) {

			}
			global.searchPlugin();
			setting.setRegs(global.getRegsCollection());
			context.setAttribute(ApplicationGlobal.APP_CONST, global);
			if (xml.getDatabaseItem().getConnectionString().isPool())
				ConnectionProvider.setCurrent(ContextConnectionCreator.Instance);
			GlobalProvider.getDbProvider().registerDriver();
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		ServletContext context = config.getServletContext();
		Object obj = context.getAttribute(ApplicationGlobal.APP_CONST);
		if (obj == null) {
			initialize(request, response);
		}

		chain.doFilter(request, response);
	}

	public void init(FilterConfig config) throws ServletException {
		this.config = config;
	}
}
