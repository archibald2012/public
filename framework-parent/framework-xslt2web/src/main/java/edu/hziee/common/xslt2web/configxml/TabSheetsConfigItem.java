package edu.hziee.common.xslt2web.configxml;

import java.util.HashMap;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TabSheetsConfigItem {
	private Node configNode;
	private HashMap<String, TabSheetConfigItem> tabSheet;

	public TabSheetsConfigItem() {
		this.tabSheet = new HashMap<String, TabSheetConfigItem>();
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final TabSheetConfigItem getTabSheet(String id) {
		return this.tabSheet.get(id);
	}

	public void readXml(Node node) {
		this.configNode = node;

		NodeList childNodes = node.getChildNodes();
		int count = childNodes.getLength();
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if ("tk:TabSheet".equals(childNode.getNodeName())) {
				TabSheetConfigItem item = TabSheetConfigItem.parse(childNode);
				if (this.tabSheet.containsKey(item.getID()))
					this.tabSheet.put(item.getID(), item);
			}
		}
	}

	public static TabSheetsConfigItem parse(Node node) {
		if (node == null)
			return null;
		TabSheetsConfigItem result = new TabSheetsConfigItem();
		result.readXml(node);
		return result;
	}
}