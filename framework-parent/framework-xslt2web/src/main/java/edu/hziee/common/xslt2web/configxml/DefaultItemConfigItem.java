package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import edu.hziee.common.xslt2web.data.BaseDataSet;
import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class DefaultItemConfigItem {
	private Node configNode;
	private String value;
	private PageStyle source;
	private PageStyle dest;
	private boolean post;
	private boolean needParse;
	private String prompt;

	public DefaultItemConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final String getValue() {
		return this.value;
	}

	public final PageStyle getSource() {
		return this.source;
	}

	public final PageStyle getDest() {
		return this.dest;
	}

	public final boolean isPost() {
		return this.post;
	}

	public final boolean isNeedParse() {
		return this.needParse;
	}

	public final String getPrompt() {
		return this.prompt;
	}

	public void readXml(Node node) {
		this.configNode = node;
		this.value = node.getTextContent();

		NamedNodeMap attributes = node.getAttributes();
		String sourceValue = XmlUtil.getNodeContent(attributes
				.getNamedItem("Source"), "Custom");
		this.source = (PageStyle) Enum.valueOf(PageStyle.class, sourceValue);
		String destValue = XmlUtil.getNodeContent(attributes
				.getNamedItem("Dest"), "Custom");
		this.dest = (PageStyle) Enum.valueOf(PageStyle.class, destValue);
		this.post = XmlUtil.getNodeBoolValue(attributes
				.getNamedItem("IsPost"), false);
		this.needParse = XmlUtil.getNodeBoolValue(attributes
				.getNamedItem("NeedParse"), false);
		this.prompt = XmlUtil.getNodeContent(attributes.getNamedItem("Prompt"));
	}
	
	public String getCustomValue(BaseDataSet hostDataSet)
    {
        return ""; // needParse ? fExpression.Execute(hostDataSet) : fValue;
    }

	public static DefaultItemConfigItem parse(Node node) {
		if (node == null)
			return null;
		DefaultItemConfigItem result = new DefaultItemConfigItem();
		result.readXml(node);
		return result;
	}
}