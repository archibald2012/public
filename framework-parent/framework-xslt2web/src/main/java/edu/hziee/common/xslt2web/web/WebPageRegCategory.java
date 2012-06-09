package edu.hziee.common.xslt2web.web;

import yjc.toolkit.sys.RegCategory;

public class WebPageRegCategory extends
		RegCategory<WebBasePage, WebPageAnnotation> {

	private static final long serialVersionUID = 1L;

	public final static String REG_NAME = "WEB_PAGE";
	private final static String DISPLAY_NAME = "��ҳ";

	public WebPageRegCategory() {
		super(REG_NAME, DISPLAY_NAME, "webpage", WebPageAnnotation.class,
				WebPageAttribute.class);

		this.addRegClass(WebTreeDialogPage.class);
	}
}
