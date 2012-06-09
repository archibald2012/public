package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class SubItemConfigItem {
	private Node configNode;
	private int length;
	private int subTotalLength;

	public SubItemConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final int getLength() {
		return this.length;
	}

	public final int getSubTotalLength() {
		return subTotalLength;
	}

	final void setSubtotalLength(int subTotalLength) {
		this.subTotalLength = subTotalLength;
	}

	public void readXml(Node node) {
		this.configNode = node;

		NamedNodeMap attributes = node.getAttributes();
		this.length = XmlUtil.getNodeIntValue(
				attributes.getNamedItem("Length"), 0);
	}

	public static SubItemConfigItem parse(Node node) {
		if (node == null)
			return null;
		SubItemConfigItem result = new SubItemConfigItem();
		result.readXml(node);
		return result;
	}
}