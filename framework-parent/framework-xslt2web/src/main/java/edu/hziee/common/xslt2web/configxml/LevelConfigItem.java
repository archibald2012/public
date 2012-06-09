package edu.hziee.common.xslt2web.configxml;

import java.util.ArrayList;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class LevelConfigItem {
	private Node configNode;
	private int totalLength;
	private ArrayList<SubItemConfigItem> subItem;
	private int[] level;

	public LevelConfigItem() {
		this.subItem = new ArrayList<SubItemConfigItem>();
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final int getTotalLength() {
		return this.totalLength;
	}

	public final ArrayList<SubItemConfigItem> getSubItem() {
		return this.subItem;
	}
	
	public final int[] getLevel() {
		return level;
	}

	public final int getLevel(String value) {
		int length = value.length();
		return length == 0 ? 0 : level[value.length() - 1];
	}
	
	public final String getParentValue(int level, String value) {
		if (level == 0)
			return "0";
		else {
			int len = subItem.get(level - 1).getSubTotalLength();
			return value.substring(0, len);
		}
	}
	
	public final int isLeaf(int level) {
		return level == subItem.size() - 1 ? 1 : 0;
	}

	public void readXml(Node node) {
		this.configNode = node;

		NamedNodeMap attributes = node.getAttributes();
		this.totalLength = XmlUtil.getNodeIntValue(attributes
				.getNamedItem("TotalLength"), 0);

		NodeList childNodes = node.getChildNodes();
		int count = childNodes.getLength();
		int len = 0;
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if ("SubItem".equals(childNode.getNodeName())) {
				SubItemConfigItem item = SubItemConfigItem.parse(childNode);
				len += item.getLength();
				item.setSubtotalLength(len);
				this.subItem.add(item);
			}
		}

		level = new int[totalLength];
		int j = 0;
		for (int i = 0; i < subItem.size(); ++i)
			for (; j < subItem.get(i).getSubTotalLength(); ++j)
				level[j] = i;
	}

	public static LevelConfigItem parse(Node node) {
		if (node == null)
			return null;
		LevelConfigItem result = new LevelConfigItem();
		result.readXml(node);
		return result;
	}
}