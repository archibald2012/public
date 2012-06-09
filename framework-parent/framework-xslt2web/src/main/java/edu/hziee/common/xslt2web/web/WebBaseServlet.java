package edu.hziee.common.xslt2web.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import yjc.toolkit.exception.ErrorPageException;
import yjc.toolkit.exception.ToolkitException;
import yjc.toolkit.sys.AppSetting;
import yjc.toolkit.sys.ApplicationGlobal;
import yjc.toolkit.sys.SessionGlobal;
import yjc.toolkit.sysutil.ErrorUtil;
import yjc.toolkit.sysutil.StringUtil;
import yjc.toolkit.xml.ExceptionSource;
import yjc.toolkit.xml.ExceptionSourceFactory;
import yjc.toolkit.xml.HtmlPosition;
import yjc.toolkit.xml.ITransformAll;
import yjc.toolkit.xml.IXmlSource;

public abstract class WebBaseServlet extends HttpServlet {
	private ApplicationGlobal appGbl;
	private SessionGlobal sessionGbl;
	private ExceptionSource exceptionSource;
	private int errorID;

	public WebBaseServlet() {
		super();
	}

	protected final ApplicationGlobal getAppGbl() {
		return appGbl;
	}

	protected final SessionGlobal getSessionGbl() {
		return sessionGbl;
	}

	private void loadPage(HttpServletRequest req, HttpServletResponse resp,
			boolean isGet) throws ServletException, IOException {
		ServletContext context = this.getServletContext();
		appGbl = (ApplicationGlobal) context
				.getAttribute(ApplicationGlobal.APP_CONST);
		HttpSession session = req.getSession();
		sessionGbl = (SessionGlobal) session
				.getAttribute(SessionGlobal.SESSION_CONST);

		WebBasePage page = createPage(req, resp);
		page.setGetMethod(isGet);
		try {
			page.loadPage();
		} catch (ErrorPageException ex) {
			getSessionGbl().getErrorPage().setErrorPage(ex);
			String xslt = getSessionGbl().getTransform().getIeFiles()
					.getError();
			handleErrorPage(xslt, xslt, getSessionGbl().getErrorPage(), isGet,
					req, resp);
		} catch (ToolkitException ex) {
			setExceptionSource(ex, false, req);
			String xslt = "../Tk2Template/Exception.xslt";
			handleErrorPage(xslt, xslt, exceptionSource, isGet, req, resp);
		} catch (Exception ex) {
			if ("".equals(ex.getMessage()) && ex.getCause() != null)
				setExceptionSource((Exception) ex.getCause(), false, req);
			else
				setExceptionSource(ex, true, req);
			if (AppSetting.getCurrent().isShowException()) {
				String xslt = "../Tk2Template/Exception.xslt";
				handleErrorPage(xslt, xslt, exceptionSource, isGet, req, resp);
			} else {
				getSessionGbl().getErrorPage().setErrorPage(
						new InternalErrorPageException(errorID));
				String xslt = getSessionGbl().getTransform().getIeFiles()
						.getError();
				handleErrorPage(xslt, xslt, getSessionGbl().getErrorPage(),
						isGet, req, resp);
			}
		}
	}

	private void setExceptionSource(Exception ex, boolean writeFile,
			HttpServletRequest request) {
		if (writeFile)
			errorID = ErrorUtil.getID(AppSetting.getCurrent().getErrorPath());
		String queryString = request.getQueryString();
		String selfURL = request.getRequestURL().toString()
				+ (StringUtil.isEmpty(queryString) ? "" : "?" + queryString);
		exceptionSource = ExceptionSourceFactory.createSource(StringUtil
				.getDefaultStr(request.getParameter("Source")), this.getClass()
				.toString(), selfURL, sessionGbl.getInfo(), ex);
	}

	private void handleErrorPage(String ieErrorXslt, String navErrorXslt,
			IXmlSource source, boolean isGet, HttpServletRequest request,
			HttpServletResponse response) {
		ITransformAll transform = getSessionGbl().getTransform();

		transform.getIeFiles().setContent(ieErrorXslt);
		transform.getNavFiles().setContent(navErrorXslt);
		transform.setItem(HtmlPosition.Content, source);
		transform.setShowSource(!isGet ? false : WebXmlPage.VIEW_VALUE
				.equals(StringUtil.getDefaultStr(
						request.getParameter(WebXmlPage.VIEW_TAG))
						.toLowerCase()));
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		boolean isIe = request.getHeader("user-agent").indexOf("MSIE") != -1;
		if (isGet)
			writer.write(transform.transformAll(isIe));
		else {
			writer.write(transform.transformContent(isIe));
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		loadPage(req, resp, true);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		loadPage(req, resp, false);
	}

	protected abstract WebBasePage createPage(HttpServletRequest request,
			HttpServletResponse response);
}
