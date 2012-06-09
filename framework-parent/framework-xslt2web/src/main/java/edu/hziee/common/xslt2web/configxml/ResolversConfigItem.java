package edu.hziee.common.xslt2web.configxml;

import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ResolversConfigItem {
	private Node configNode;
	private ArrayList<ResolverConfigItem> resolver;

	public ResolversConfigItem() {
		this.resolver = new ArrayList<ResolverConfigItem>();
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final ArrayList<ResolverConfigItem> getResolver() {
		return this.resolver;
	}

	public void readXml(Node node) {
		this.configNode = node;

		NodeList childNodes = node.getChildNodes();
		int count = childNodes.getLength();
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if ("tk:Resolver".equals(childNode.getNodeName()))
				this.resolver.add(ResolverConfigItem.parse(childNode));
		}
	}
    
	public static ResolversConfigItem parse(Node node) {
		if (node == null)
			return null;
		ResolversConfigItem result = new ResolversConfigItem();
		result.readXml(node);
		return result;
	}
}