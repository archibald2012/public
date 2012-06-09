package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class EnvironmentConfigItem {
	private Node configNode;

	private String key;

	private String value;

	public EnvironmentConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final String getKey() {
		return this.key;
	}

	public final String getValue() {
		return this.value;
	}

	public void readXml(Node node) {
		this.configNode = node;

		NamedNodeMap attributes = node.getAttributes();
		this.key = attributes.getNamedItem("Key").getTextContent();
		this.value = attributes.getNamedItem("Value").getTextContent();
	}

	public static EnvironmentConfigItem parse(Node node) {
		if (node == null)
			return null;
		EnvironmentConfigItem result = new EnvironmentConfigItem();
		result.readXml(node);
		return result;
	}
}