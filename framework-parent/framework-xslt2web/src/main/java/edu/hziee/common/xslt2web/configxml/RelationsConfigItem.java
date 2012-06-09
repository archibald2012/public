package edu.hziee.common.xslt2web.configxml;

import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RelationsConfigItem {
	private Node configNode;
	private ArrayList<RelationConfigItem> relation;

	public RelationsConfigItem() {
		this.relation = new ArrayList<RelationConfigItem>();
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final ArrayList<RelationConfigItem> getRelation() {
		return this.relation;
	}

	public void readXml(Node node) {
		this.configNode = node;

		NodeList childNodes = node.getChildNodes();
		int count = childNodes.getLength();
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if ("tk:Relation".equals(childNode.getNodeName()))
				this.relation.add(RelationConfigItem.parse(childNode));
		}
	}

	public static RelationsConfigItem parse(Node node) {
		if (node == null)
			return null;
		RelationsConfigItem result = new RelationsConfigItem();
		result.readXml(node);
		return result;
	}
}