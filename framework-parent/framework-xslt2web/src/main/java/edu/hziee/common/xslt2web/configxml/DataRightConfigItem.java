package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class DataRightConfigItem {
	private Node configNode;
	private String rightType;
	private String ownerField;

	public DataRightConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final String getRightType() {
		return this.rightType;
	}

	public final String getOwnerField() {
		return this.ownerField;
	}

	public void readXml(Node node) {
		this.configNode = node;

		NamedNodeMap attributes = node.getAttributes();
		this.rightType = XmlUtil.getNodeContent(attributes
				.getNamedItem("RightType"));
		this.ownerField = attributes.getNamedItem("OwnerField")
				.getTextContent();
	}

	public static DataRightConfigItem parse(Node node) {
		if (node == null)
			return null;
		DataRightConfigItem result = new DataRightConfigItem();
		result.readXml(node);
		return result;
	}
}