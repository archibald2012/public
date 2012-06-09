package edu.hziee.common.xslt2web.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import yjc.toolkit.easysearch.EasySearchDataSet;
import yjc.toolkit.exception.ErrorPageException;
import yjc.toolkit.exception.ToolkitException;
import yjc.toolkit.sys.AppSetting;
import yjc.toolkit.sys.PageStyle;
import yjc.toolkit.sysutil.FileUtil;

class WebDialogSearchPage extends WebXmlHttpPage {
	//private static final String HTML = "<html>	<head>	<title>��ѯ</title>	<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">        <link rel=\"stylesheet\" type=\"text/css\" href=\"../toolkitcss/toolkit.css\"/>	<style type=\"text/css\">body {SCROLLBAR-HIGHLIGHT-COLOR: buttonface; SCROLLBAR-SHADOW-COLOR: buttonshadow; SCROLLBAR-3DLIGHT-COLOR: buttonhighlight; SCROLLBAR-TRACK-COLOR: white; SCROLLBAR-DARKSHADOW-COLOR: #eeeeee; SCROLLBAR-FACE-COLOR: buttonface;}</style>        <script language=\"javascript\" src=\"../jslib/prototype.js\"> </script>        <script language=\"javascript\" src=\"../jslib/toolkitlib.js\"> </script>        <script language=\"javascript\" src=\"../toolkitjs/toolkit.js\"> </script></head><body leftmargin=\"2\" topmargin=\"2\" marginwidth=\"0\" marginheight=\"0\">	<table width=\"100%%\" height=\"100%%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">		<tr>			<td valign=\"top\" id=\"Result\" height=\"100%%\">			%s			</td>		</tr>	</table></body></html>";
	private InternalDailogTransform transform;

	public WebDialogSearchPage(HttpServlet servlet, HttpServletRequest request,
			HttpServletResponse response) {
		super(servlet, request, response);

		// setSupportLogin(true);
		setSource(new EasySearchDataSet());
		setStyle(PageStyle.Custom);
		setOperation(EasySearchDataSet.DIALOG);
	}

	@Override
	protected void doPost() {
		super.doPost();

		getXsltFile();
		String html = getTransform().transformContent(isIe());
		if (AppSetting.getCurrent().isDebug()) {
			String fileName = FileUtil.combin(AppSetting.getCurrent()
					.getXmlPath(), "EasySearch.html");
			FileUtil.saveFile(fileName, html, "GBK");
		}
		getResponseWriter().write(html);
	}

	@Override
	protected void initPageData() {
		super.initPageData();
		
		transform = new InternalDailogTransform();
		transform.setPost(isPost());
		setTransform(transform);
	}

	@Override
	protected void handleBaseException(RuntimeException ex) {
		setTransform(getSessionGlobal().getTransform());
		super.handleBaseException(ex);
	}

	@Override
	protected void handleErrorPage(ErrorPageException ex) {
		setTransform(getSessionGlobal().getTransform());
		super.handleErrorPage(ex);
	}

	@Override
	protected void handleToolkit(ToolkitException ex) {
		setTransform(getSessionGlobal().getTransform());
		super.handleToolkit(ex);
	}

}
