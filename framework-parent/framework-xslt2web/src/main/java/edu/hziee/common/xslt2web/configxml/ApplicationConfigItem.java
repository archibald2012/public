package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class ApplicationConfigItem {
	private Node configNode;
	private ApplicationType type;
	private String appStartClass;
	private String sessionStartClass;
	private String path;

	public ApplicationConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final ApplicationType getType() {
		return this.type;
	}

	public final String getAppStartClass() {
		return this.appStartClass;
	}

	public final String getSessionStartClass() {
		return this.sessionStartClass;
	}

	public final String getPath() {
		return this.path;
	}

	public void readXml(Node node) {
		this.configNode = node;

		NamedNodeMap attributes = node.getAttributes();
		String typeValue = XmlUtil.getNodeContent(attributes
				.getNamedItem("Type"), "Web");
		this.type = (ApplicationType) Enum.valueOf(ApplicationType.class,
				typeValue);

		NodeList childNodes = node.getChildNodes();
		int count = childNodes.getLength();
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if ("AppStartClass".equals(childNode.getNodeName()))
				this.appStartClass = childNode.getTextContent();
			else if ("SessionStartClass".equals(childNode.getNodeName()))
				this.sessionStartClass = childNode.getTextContent();
			else if ("Path".equals(childNode.getNodeName()))
				this.path = childNode.getTextContent();
		}
	}

	public static ApplicationConfigItem parse(Node node) {
		if (node == null)
			return null;
		ApplicationConfigItem result = new ApplicationConfigItem();
		result.readXml(node);
		return result;
	}
}