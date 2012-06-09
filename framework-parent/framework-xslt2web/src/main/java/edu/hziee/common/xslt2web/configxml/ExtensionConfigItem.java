package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class ExtensionConfigItem {
	private Node configNode;
	private String value;
	private String expression;
	private String sortField;
	private boolean span;

	public ExtensionConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final String getValue() {
		return this.value;
	}

	public final String getExpression() {
		return this.expression;
	}

	public final String getSortField() {
		return sortField;
	}

	public final boolean isSpan() {
		return span;
	}

	public void readXml(Node node) {
		this.configNode = node;
		this.value = node.getTextContent();

		NamedNodeMap attributes = node.getAttributes();
		this.expression = XmlUtil.getNodeContent(attributes
				.getNamedItem("Expression"));
		this.sortField = XmlUtil.getNodeContent(attributes
				.getNamedItem("SortField"));
		this.span = XmlUtil.getNodeBoolValue(attributes.getNamedItem("Span"), false);
	}

	public static ExtensionConfigItem parse(Node node) {
		if (node == null)
			return null;
		ExtensionConfigItem result = new ExtensionConfigItem();
		result.readXml(node);
		return result;
	}
}