package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class TabSheetConfigItem extends MarcoConfigItem {
	private String iD;
	private String title;

	public TabSheetConfigItem() {
	}

	public final String getID() {
		return this.iD;
	}

	public final String getTitle() {
		return this.title;
	}

	public void readXml(Node node) {
		super.readXml(node);

		NamedNodeMap attributes = node.getAttributes();
		this.iD = attributes.getNamedItem("ID").getTextContent();
		this.title = attributes.getNamedItem("Title").getTextContent();
	}

	public static TabSheetConfigItem parse(Node node) {
		if (node == null)
			return null;
		TabSheetConfigItem result = new TabSheetConfigItem();
		result.readXml(node);
		return result;
	}
}