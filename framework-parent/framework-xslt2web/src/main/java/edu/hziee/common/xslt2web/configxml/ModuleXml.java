package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.hziee.common.xslt2web.data.AbstractXmlDataAdapter;
import edu.hziee.common.xslt2web.data.ResolverUtil;
import edu.hziee.common.xslt2web.sysutil.FileUtil;
import edu.hziee.common.xslt2web.sysutil.XmlUtil;

import edu.hziee.common.xslt2web.sys.AppSetting;
import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sys.XmlConfig;

public class ModuleXml extends XmlConfig {
	public static final int PAGE_STYLE_LENGTH = 6;
	private String version;
	private ModuleConfigItem module;

	public ModuleXml() {
		super();
	}

	@Override
	protected String getFileName(String xmlFile) {
		return FileUtil.combin(AppSetting.getCurrent().getModuleXmlPath(),
				xmlFile + ".xml");
	}

	public final String getversion() {
		return this.version;
	}

	public final ModuleConfigItem getModule() {
		return this.module;
	}

	public String getDefaultPage(AbstractXmlDataAdapter resolver,
			boolean isPost, PageStyle style, String saveMethod, String retURL) {
		String url = getDefaultPage(resolver, isPost, style);
		if ("".equals(url)) {
			if (!"".equals(retURL))
				return retURL;

			if (!isPost)
				return String.format("../toolkit/weblistxmlpage?Source=%s",
						getXmlFile());
			else
				return ResolverUtil.getDefaultXmlURL(resolver, saveMethod,
						getXmlFile(), style);
		} else
			return url;
	}

	public String getDefaultPage(AbstractXmlDataAdapter resolver,
			boolean isPost, PageStyle style) {
		String url = module.getDefaultPage() == null ? "" : module
				.getDefaultPage().getDefaultPage(resolver, isPost, style,
						getXmlFile());
		return url;
	}

	public String getAlertString(PageStyle style, String operation) {
		if (module.isPrompt()) {
			String prompt = module.getDefaultPage() == null ? "" : module
					.getDefaultPage().getAlertString(style);
			if (!"".equals(prompt))
				return prompt;
			switch (style) {
			case Insert:
				return "记录已经保存！";
			case Update:
				return "修改已经保存！";
			}
		}
		return "";
	}

	private XsltFileConfigItem getXsltFileConfigItem(PageStyle style,
			String operation) {
		XsltFilesConfigItem xslts = this.module.getXsltFiles();
		return xslts == null ? null : xslts.getXsltFileItem(style, operation);
	}

	public final String getXsltFile(PageStyle style, String operation) {
		XsltFileConfigItem item = getXsltFileConfigItem(style, operation);
		return item == null ? "" : item.getValue();
	}

	public final boolean getPageSupportLogin(PageStyle style, String operation) {
		XsltFileConfigItem item = getXsltFileConfigItem(style, operation);
		return item == null ? true : item.isSupportLogin();
	}

	public final String getPageTransformAll(PageStyle style, String operation) {
		XsltFileConfigItem item = getXsltFileConfigItem(style, operation);
		return item == null ? "" : item.getTransformAll();
	}

	public final boolean getSupportDoubleTransform(PageStyle style,
			String operation) {
		XsltFileConfigItem item = getXsltFileConfigItem(style, operation);
		return item == null ? true : item.isSupportDoubleTransfrom();
	}

	public final String getDataXslt(PageStyle style, String operation) {
		XsltFileConfigItem item = getXsltFileConfigItem(style, operation);
		return item == null ? "" : item.getDataXslt();
	}

	public final String getDefaultXsltTemplate(PageStyle style, String operation) {
		XsltFileConfigItem item = getXsltFileConfigItem(style, operation);
		return item == null ? "" : item.getDefaultXsltTemplate();
	}

	public final boolean getCheckSubmit(PageStyle style, String operation) {
		XsltFileConfigItem item = getXsltFileConfigItem(style, operation);
		if (item != null)
			return item.isCheckSubmit();
		else
			return style == PageStyle.Insert || style == PageStyle.Update;
	}

	public final String getTabCondition(String tab) {
		if (module.getTabSheets() == null)
			return "";
		TabSheetConfigItem item = module.getTabSheets().getTabSheet(tab);
		if (item == null)
			return "";
		return item.toString();
	}

	public final Object getSubFunctionKey(PageStyle style, String operation) {
		if (module.getFunctionRight() == null)
			return "";
		else
			return module.getFunctionRight().getSubFunctionRight(style,
					operation);
	}

	public final void loadFile(String xmlFile) {
		loadXmlFile(xmlFile);
		Node node = getToolkitNode();

		NamedNodeMap attributes = node.getAttributes();
		this.version = XmlUtil.getNodeContent(attributes
				.getNamedItem("version"), "2.0");

		NodeList childNodes = node.getChildNodes();
		int count = childNodes.getLength();
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if ("tk:Module".equals(childNode.getNodeName()))
				this.module = ModuleConfigItem.parse(childNode);
		}
	}
	
	public static final ModuleXml loadModuleFile(String xmlFile) {
		ModuleXml result = new ModuleXml();
		result.loadFile(xmlFile);
		return result;
	}
}