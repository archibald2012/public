package edu.hziee.common.xslt2web.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import yjc.toolkit.exception.ErrorPageException;
import yjc.toolkit.exception.InformationException;
import yjc.toolkit.exception.RedirectException;
import yjc.toolkit.exception.ToolkitException;
import yjc.toolkit.sys.AppSetting;
import yjc.toolkit.sys.ApplicationGlobal;
import yjc.toolkit.sys.IWebData;
import yjc.toolkit.sys.PageStyle;
import yjc.toolkit.sys.SessionGlobal;
import yjc.toolkit.sysutil.ErrorUtil;
import yjc.toolkit.sysutil.FileUtil;
import yjc.toolkit.sysutil.StringUtil;
import yjc.toolkit.sysutil.WebUtil;
import yjc.toolkit.xml.ExceptionSource;
import yjc.toolkit.xml.ExceptionSourceFactory;
import yjc.toolkit.xml.IXmlSource;

public abstract class WebBasePage implements IWebData {
	private HttpServlet servlet;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private HttpSession session;

	private ApplicationGlobal appGbl;
	private SessionGlobal sessionGbl;

	private boolean supportLogin;
	private Object subFunctionKey;
	private String selfURL;
	private String encodeURL;
	private String retURL;
	private PageStyle style;

	private boolean ie;
	private boolean getMethod;
	private boolean supportCheckSubmit;
	private boolean error;
	private int errorID;
	private ExceptionSource exceptionSource;

	private WebPageAnnotation annotation;

	protected WebBasePage(HttpServlet servlet, HttpServletRequest request,
			HttpServletResponse response) {
		this.servlet = servlet;
		this.request = request;
		this.response = response;
		this.session = request.getSession();
		this.appGbl = (ApplicationGlobal) servlet.getServletContext()
				.getAttribute(ApplicationGlobal.APP_CONST);
		this.sessionGbl = (SessionGlobal) session
				.getAttribute(SessionGlobal.SESSION_CONST);
		this.getMethod = "GET".equals(request.getMethod().toUpperCase());

		setURL();
		setExplorer(request);
	}

	private void setExplorer(HttpServletRequest request) {
		String agent = request.getHeader("user-agent");
		ie = agent.indexOf("MSIE") != -1;
	}

	private void setURL() {
		selfURL = WebUtil.getSelfURL(request);
		retURL = WebUtil.getRetURL(request);
		encodeURL = WebUtil.encodeURL(selfURL);
	}

	final void setGetMethod(boolean getMethod) {
		this.getMethod = getMethod;
	}

	protected void doGet() {
	}

	protected void doPost() {
	}

	protected void initPageData() {
	}

	protected void checkFunctionRight() {
	}

	protected void checkSubmit() {
	}

	public final void loadPage() {
		try {
			error = false;
			initPageData();
			if (supportLogin)
				sessionGbl.getRights().getLoginRight().checkLogin(
						sessionGbl.getInfo().getUserID(), encodeURL);
			checkFunctionRight();
			if (getMethod)
				doGet();
			else {
				if (supportCheckSubmit)
					checkSubmit();
				doPost();
			}
		} catch (ErrorPageException ex) {
			error = true;
			handleErrorPage(ex);
		} catch (InformationException ex) {
			error = true;
			handleInformation(ex);
		} catch (RedirectException ex) {
			error = true;
			handleRedirect(ex);
		} catch (ToolkitException ex) {
			error = true;
			handleToolkit(ex);
		} catch (RuntimeException ex) {
			// if (ex is System.Threading.ThreadAbortException)
			// return;
			error = true;
			handleBaseException(ex);
		}
	}

	public final boolean isIe() {
		return ie;
	}

	public final HttpServlet getServlet() {
		return servlet;
	}

	public final HttpServletRequest getRequest() {
		return request;
	}

	public final HttpServletResponse getResponse() {
		return response;
	}

	public final PrintWriter getResponseWriter() {
		try {
			return response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ToolkitException(e, e.getMessage());
		}
	}

	public final PageStyle getStyle() {
		return style;
	}

	public final void setStyle(PageStyle style) {
		this.style = style;
	}

	public final String getSelfURL() {
		return selfURL;
	}

	public final String getEncodeURL() {
		return encodeURL;
	}

	public final String getRetURL() {
		return retURL;
	}

	public final boolean isGet() {
		return getMethod;
	}

	public final boolean isPost() {
		return !getMethod;
	}

	public final boolean isSupportLogin() {
		return supportLogin;
	}

	public final int getErrorID() {
		return errorID;
	}

	public final IXmlSource getExceptionSource() {
		return exceptionSource;
	}

	public final void setSupportLogin(boolean supportLogin) {
		this.supportLogin = supportLogin;
	}

	public final Object getSubFunctionKey() {
		return subFunctionKey;
	}

	public final void setSubFunctionKey(Object subFunctionKey) {
		this.subFunctionKey = subFunctionKey;
	}

	public final HttpSession getSession() {
		return session;
	}

	public final ApplicationGlobal getAppGlobal() {
		return appGbl;
	}

	public final SessionGlobal getSessionGlobal() {
		return sessionGbl;
	}

	public final boolean isSupportCheckSubmit() {
		return supportCheckSubmit;
	}

	public final void setSupportCheckSubmit(boolean supportCheckSubmit) {
		this.supportCheckSubmit = supportCheckSubmit;
	}

	public final WebPageAnnotation getAnnotation() {
		if (annotation == null)
			annotation = this.getClass().getAnnotation(WebPageAnnotation.class);

		return annotation;
	}

	private void setExceptionSource(Exception ex, boolean writeFile) {
		exceptionSource = ExceptionSourceFactory.createSource(StringUtil.getDefaultStr(request
				.getParameter("Source")), this.getClass().toString(), request
				.getRequestURI(), getSessionGlobal().getInfo(), ex);
		if (writeFile) {
			String fileName = FileUtil.combin(AppSetting.getCurrent()
					.getErrorPath(), Integer.toString(errorID) + ".xml");
			FileUtil.saveFile(fileName, exceptionSource.getXmlString(), "GBK");
		}
	}

	protected void handleErrorPage(ErrorPageException ex) {
		throw ex;
	}

	protected void handleInformation(InformationException ex) {
	}

	protected void handleRedirect(RedirectException ex) {
		try {
			response.sendRedirect(ex.getUrl());
		} catch (IOException e) {
			e.printStackTrace();
		}
		// response.redirect(ex.getUrl(), true);
	}

	protected void handleToolkit(ToolkitException ex) {
		setExceptionSource(ex, false);
		throw ex;
	}

	protected void handleBaseException(RuntimeException ex) {
		errorID = ErrorUtil.getID(AppSetting.getCurrent().getErrorPath());
		setExceptionSource(ex, true);

		throw new RuntimeException("", ex);
	}

	public final boolean isError() {
		return error;
	}
}
