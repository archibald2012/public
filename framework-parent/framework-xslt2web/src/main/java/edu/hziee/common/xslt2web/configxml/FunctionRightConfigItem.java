package edu.hziee.common.xslt2web.configxml;

import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.hziee.common.xslt2web.right.FunctionRightType;
import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class FunctionRightConfigItem {
	private Node configNode;
	private String functionKey;
	private FunctionRightType funcType;
	private SubFunctionRightConfigItem[] pageRights;
	private HashMap<String, SubFunctionRightConfigItem> customRights;
	private int count;

	public FunctionRightConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final String getFunctionKey() {
		return this.functionKey;
	}

	public final FunctionRightType getFuncType() {
		return this.funcType;
	}

	public final SubFunctionRightConfigItem getSubFunctionRight(
			PageStyle style, String operation) {
		if (count == 0)
			return null;
		if (style == PageStyle.Custom)
			return customRights.get(operation);
		else
			return this.pageRights[style.ordinal()];
	}

	public void readXml(Node node) {
		this.configNode = node;

		NamedNodeMap attributes = node.getAttributes();
		this.functionKey = attributes.getNamedItem("FunctionKey")
				.getTextContent();
		String funcTypeValue = XmlUtil.getNodeContent(attributes
				.getNamedItem("FuncType"), "None");
		this.funcType = (FunctionRightType) Enum.valueOf(
				FunctionRightType.class, funcTypeValue);

		NodeList childNodes = node.getChildNodes();
		int count = childNodes.getLength();
		pageRights = new SubFunctionRightConfigItem[ModuleXml.PAGE_STYLE_LENGTH];
		customRights = new HashMap<String, SubFunctionRightConfigItem>();
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if ("tk:SubFunctionRight".equals(childNode.getNodeName())) {
				++this.count;
				SubFunctionRightConfigItem item = SubFunctionRightConfigItem
						.parse(childNode);
				if (item.getPageStyle() == PageStyle.Custom)
					customRights.put(item.getOperation(), item);
				else
					pageRights[item.getPageStyle().ordinal()] = item;
			}
		}
	}

	public static FunctionRightConfigItem parse(Node node) {
		if (node == null)
			return null;
		FunctionRightConfigItem result = new FunctionRightConfigItem();
		result.readXml(node);
		return result;
	}
}