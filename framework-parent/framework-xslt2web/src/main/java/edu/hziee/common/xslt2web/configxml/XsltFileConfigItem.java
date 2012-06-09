package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class XsltFileConfigItem {
	private Node configNode;
	private String value;
	private PageStyle type;
	private String operation;
	private boolean supportLogin;
	private boolean checkSubmit;
	private boolean supportDoubleTransfrom;
	private String transformAll;
	private String dataXslt;
	private String defaultXsltTemplate;

	public XsltFileConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final String getValue() {
		return this.value;
	}

	public final PageStyle getType() {
		return this.type;
	}

	public final String getOperation() {
		return this.operation;
	}

	public final boolean isSupportLogin() {
		return this.supportLogin;
	}

	public final boolean isCheckSubmit() {
		return this.checkSubmit;
	}

	public final boolean isSupportDoubleTransfrom() {
		return this.supportDoubleTransfrom;
	}

	public final String getTransformAll() {
		return this.transformAll;
	}

	public final String getDataXslt() {
		return this.dataXslt;
	}

	public final String getDefaultXsltTemplate() {
		return this.defaultXsltTemplate;
	}

	public void readXml(Node node) {
		this.configNode = node;
		this.value = node.getTextContent();

		NamedNodeMap attributes = node.getAttributes();
		String typeValue = XmlUtil.getNodeContent(attributes
				.getNamedItem("Type"), "Custom");
		this.type = (PageStyle) Enum.valueOf(PageStyle.class, typeValue);
		this.operation = XmlUtil.getNodeContent(attributes
				.getNamedItem("Operation"));
		this.supportLogin = XmlUtil.getNodeBoolValue(attributes
				.getNamedItem("SupportLogin"), true);
		this.checkSubmit = XmlUtil.getNodeBoolValue(attributes
				.getNamedItem("CheckSubmit"), type == PageStyle.Insert
				|| type == PageStyle.Update);
		this.supportDoubleTransfrom = XmlUtil.getNodeBoolValue(attributes
				.getNamedItem("SupportDoubleTransfrom"), true);
		this.transformAll = XmlUtil.getNodeContent(attributes
				.getNamedItem("TransformAll"));
		this.dataXslt = XmlUtil.getNodeContent(attributes
				.getNamedItem("DataXslt"));
		this.defaultXsltTemplate = XmlUtil.getNodeContent(attributes
				.getNamedItem("DefaultXsltTemplate"));
	}

	public static XsltFileConfigItem parse(Node node) {
		if (node == null)
			return null;
		XsltFileConfigItem result = new XsltFileConfigItem();
		result.readXml(node);
		return result;
	}
}