package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class OtherConfigItem {
	private Node configNode;
	private boolean useInternalXslt;
	private int maxExcelRecords;

	public OtherConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final boolean isUseInternalXslt() {
		return this.useInternalXslt;
	}

	public final int getMaxExcelRecords() {
		return this.maxExcelRecords;
	}

	public void readXml(Node node) {
		this.configNode = node;

		NamedNodeMap attributes = node.getAttributes();
		this.useInternalXslt = XmlUtil.getNodeBoolValue(attributes
				.getNamedItem("UseInternalXslt"), false);

		NodeList childNodes = node.getChildNodes();
		int count = childNodes.getLength();
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if ("MaxExcelRecords".equals(childNode.getNodeName()))
				this.maxExcelRecords = XmlUtil.getNodeIntValue(childNode, 0);
		}
	}

	public static OtherConfigItem parse(Node node) {
		if (node == null)
			return null;
		OtherConfigItem result = new OtherConfigItem();
		result.readXml(node);
		return result;
	}
}