package edu.hziee.common.xslt2web.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import yjc.toolkit.easysearch.EasySearchDataSet;
import yjc.toolkit.sys.AppSetting;
import yjc.toolkit.sys.PageStyle;
import yjc.toolkit.sysutil.FileUtil;
import yjc.toolkit.sysutil.XslTransformUtil;

public class WebEasySearchPage extends WebXmlHttpPage {

	public WebEasySearchPage(HttpServlet servlet, HttpServletRequest request,
			HttpServletResponse response) {
		super(servlet, request, response);

		// setSupportLogin(true);
		setSource(new EasySearchDataSet());
		setStyle(PageStyle.Custom);
		setOperation(EasySearchDataSet.FAST_SEARCH);
	}

	@Override
	protected void doPost() {
		getHttpPostPage().post(getStyle(), getOperation(), getRequest(),
				getPostDataSet());
		String xml = getXmlString();
		getXsltFile();
		xml = XslTransformUtil.transform(xml, getTransform().getIeFiles()
				.getContent());
		//Transform(xml, Transform.IeFiles.Content, TransformSetting.InternalXslt);
		if (AppSetting.getCurrent().isDebug()) {
			String fileName = FileUtil.combin(AppSetting.getCurrent()
					.getXmlPath(), "EasySearch.html");
			FileUtil.saveFile(fileName, xml, "GBK");
		}
		getResponseWriter().write(xml);
	}

}
