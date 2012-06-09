package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class HtmlCtrlConfigItem {
	private Node configNode;
	private HtmlCtrlType htmlCtrl;
	private int order;

	public HtmlCtrlConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final HtmlCtrlType getHtmlCtrl() {
		return this.htmlCtrl;
	}

	public final int getOrder() {
		return this.order;
	}

	public void readXml(Node node) {
		this.configNode = node;

		NamedNodeMap attributes = node.getAttributes();
		String htmlCtrlValue = XmlUtil.getNodeContent(attributes
				.getNamedItem("HtmlCtrl"), "Text");
		this.htmlCtrl = (HtmlCtrlType) Enum.valueOf(HtmlCtrlType.class,
				htmlCtrlValue);
		this.order = XmlUtil.getNodeIntValue(attributes.getNamedItem("Order"),
				0);
	}

	public static HtmlCtrlConfigItem parse(Node node) {
		if (node == null)
			return null;
		HtmlCtrlConfigItem result = new HtmlCtrlConfigItem();
		result.readXml(node);
		return result;
	}
}