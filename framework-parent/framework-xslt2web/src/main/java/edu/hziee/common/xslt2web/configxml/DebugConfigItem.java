package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class DebugConfigItem {
	private Node configNode;
	private boolean debug;
	private boolean showException;
	private boolean useCache;

	public DebugConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final boolean isDebug() {
		return this.debug;
	}

	public final boolean isShowException() {
		return this.showException;
	}

	public final boolean isUseCache() {
		return this.useCache;
	}

	public void readXml(Node node) {
		this.configNode = node;

		NamedNodeMap attributes = node.getAttributes();
		this.debug = XmlUtil.getNodeBoolValue(attributes.getNamedItem("Debug"),
				true);
		this.showException = XmlUtil.getNodeBoolValue(attributes
				.getNamedItem("ShowException"), true);
		this.useCache = XmlUtil.getNodeBoolValue(attributes
				.getNamedItem("UseCache"), false);
	}

	public static DebugConfigItem parse(Node node) {
		if (node == null)
			return null;
		DebugConfigItem result = new DebugConfigItem();
		result.readXml(node);
		return result;
	}
}