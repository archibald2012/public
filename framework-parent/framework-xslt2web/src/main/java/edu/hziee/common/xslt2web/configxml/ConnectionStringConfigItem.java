package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class ConnectionStringConfigItem {
	private Node configNode;
	private String value;
	private boolean encode;
	private boolean pool;
	private String user;
	private String password;

	public ConnectionStringConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final String getValue() {
		return this.value;
	}

	public final boolean isEncode() {
		return this.encode;
	}

	public final boolean isPool() {
		return this.pool;
	}

	public final String getUser() {
		return user;
	}

	public final String getPassword() {
		return password;
	}

	public void readXml(Node node) {
		this.configNode = node;
		this.value = node.getTextContent();

		NamedNodeMap attributes = node.getAttributes();
		this.encode = XmlUtil.getNodeBoolValue(attributes
				.getNamedItem("Encode"), false);
		this.pool = XmlUtil.getNodeBoolValue(attributes.getNamedItem("Pool"),
				false);
		this.user = XmlUtil.getNodeContent(attributes.getNamedItem("User"));
		this.password = XmlUtil.getNodeContent(attributes
				.getNamedItem("Password"));
	}

	public static ConnectionStringConfigItem parse(Node node) {
		if (node == null)
			return null;
		ConnectionStringConfigItem result = new ConnectionStringConfigItem();
		result.readXml(node);
		return result;
	}
}