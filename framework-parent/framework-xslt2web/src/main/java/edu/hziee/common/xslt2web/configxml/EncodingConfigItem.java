package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class EncodingConfigItem {
	private Node configNode;
	private String request;
	private String responseGet;
	private String responsePost;

	public EncodingConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final String getRequest() {
		return this.request;
	}

	public final String getResponseGet() {
		return this.responseGet;
	}

	public final String getResponsePost() {
		return this.responsePost;
	}

	public void readXml(Node node) {
		this.configNode = node;

		NamedNodeMap attributes = node.getAttributes();
		this.request = XmlUtil.getNodeContent(attributes
				.getNamedItem("Request"), "UTF-8");
		this.responseGet = XmlUtil.getNodeContent(attributes
				.getNamedItem("ResponseGet"), "GBK");
		this.responsePost = XmlUtil.getNodeContent(attributes
				.getNamedItem("ResponsePost"), "UTF-8");
	}

	public static EncodingConfigItem parse(Node node) {
		if (node == null)
			return null;
		EncodingConfigItem result = new EncodingConfigItem();
		result.readXml(node);
		return result;
	}
}