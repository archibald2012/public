package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class SubFunctionRightConfigItem {
	private Node configNode;
	private PageStyle pageStyle;
	private String operation;
	private String subFunctionKey;

	public SubFunctionRightConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final PageStyle getPageStyle() {
		return this.pageStyle;
	}

	public final String getOperation() {
		return this.operation;
	}

	public final String getSubFunctionKey() {
		return this.subFunctionKey;
	}

	public void readXml(Node node) {
		this.configNode = node;

		NamedNodeMap attributes = node.getAttributes();
		String pageStyleValue = XmlUtil.getNodeContent(attributes
				.getNamedItem("PageStyle"), "Custom");
		this.pageStyle = (PageStyle) Enum.valueOf(PageStyle.class,
				pageStyleValue);
		this.operation = XmlUtil.getNodeContent(attributes
				.getNamedItem("Operation"));
		this.subFunctionKey = attributes.getNamedItem("SubFunctionKey")
				.getTextContent();
	}

	public static SubFunctionRightConfigItem parse(Node node) {
		if (node == null)
			return null;
		SubFunctionRightConfigItem result = new SubFunctionRightConfigItem();
		result.readXml(node);
		return result;
	}
}