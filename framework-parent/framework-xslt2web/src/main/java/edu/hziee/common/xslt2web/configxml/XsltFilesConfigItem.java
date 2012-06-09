package edu.hziee.common.xslt2web.configxml;

import java.util.HashMap;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.hziee.common.xslt2web.sys.PageStyle;

public class XsltFilesConfigItem {
	private Node configNode;
	private HashMap<String, XsltFileConfigItem> customs;
	private XsltFileConfigItem[] xsltFiles;
	private int count;

	public XsltFilesConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public void readXml(Node node) {
		this.configNode = node;

		NodeList childNodes = node.getChildNodes();
		int count = childNodes.getLength();
		xsltFiles = new XsltFileConfigItem[ModuleXml.PAGE_STYLE_LENGTH];
		customs = new HashMap<String, XsltFileConfigItem>();
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if ("tk:XsltFile".equals(childNode.getNodeName())) {
				++this.count;
				XsltFileConfigItem item = XsltFileConfigItem.parse(childNode);
				if (item.getType() == PageStyle.Custom)
					customs.put(item.getOperation(), item);
				else
					xsltFiles[item.getType().ordinal()] = item;
			}
		}
	}

	public XsltFileConfigItem getXsltFileItem(PageStyle style, String operation) {
		if (count == 0)
			return null;

		if (style == PageStyle.Custom)
			return customs.get(operation);
		else
			return xsltFiles[style.ordinal()];
	}

	public static XsltFilesConfigItem parse(Node node) {
		if (node == null)
			return null;
		XsltFilesConfigItem result = new XsltFilesConfigItem();
		result.readXml(node);
		return result;
	}
}