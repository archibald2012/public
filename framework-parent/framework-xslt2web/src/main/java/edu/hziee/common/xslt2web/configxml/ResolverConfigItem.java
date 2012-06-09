package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.hziee.common.xslt2web.data.AbstractXmlTableResolver;
import edu.hziee.common.xslt2web.data.BaseDataSet;
import edu.hziee.common.xslt2web.data.ResolverUtil;
import edu.hziee.common.xslt2web.sys.RegsCollection;
import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class ResolverConfigItem {
	private Node configNode;
	private boolean list;
	private boolean detailList;
	private ResolverType type;
	private boolean main;
	private String order;
	private ResolverCreateType createType;
	private String regName;
	private String xml;
	private DataRightConfigItem dataRight;

	public ResolverConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final boolean isList() {
		return this.list;
	}

	public final boolean isDetailList() {
		return this.detailList;
	}

	public final ResolverType getType() {
		return this.type;
	}

	public final boolean isMain() {
		return this.main;
	}

	public final String getOrder() {
		return this.order;
	}

	public final ResolverCreateType getCreateType() {
		return this.createType;
	}

	public final String getRegName() {
		return this.regName;
	}

	public final String getXml() {
		return this.xml;
	}

	public final String getRightType() {
		return this.dataRight == null ? "" : dataRight.getRightType();
	}

	public final String getOwnerField() {
		return this.dataRight == null ? "" : dataRight.getOwnerField();
	}
	
	public void readXml(Node node) {
		this.configNode = node;

		NamedNodeMap attributes = node.getAttributes();
		this.list = XmlUtil.getNodeBoolValue(attributes.getNamedItem("List"),
				false);
		this.detailList = XmlUtil.getNodeBoolValue(attributes
				.getNamedItem("DetailList"), false);
		String typeValue = XmlUtil.getNodeContent(attributes
				.getNamedItem("Type"), "Table");
		this.type = (ResolverType) Enum.valueOf(ResolverType.class, typeValue);
		this.main = XmlUtil.getNodeBoolValue(attributes.getNamedItem("Main"),
				false);
		this.order = XmlUtil.getNodeContent(attributes.getNamedItem("Order"));
		String createTypeValue = XmlUtil.getNodeContent(attributes
				.getNamedItem("CreateType"), "RegName");
		this.createType = (ResolverCreateType) Enum.valueOf(
				ResolverCreateType.class, createTypeValue);

		NodeList childNodes = node.getChildNodes();
		int count = childNodes.getLength();
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if ("tk:RegName".equals(childNode.getNodeName()))
				this.regName = childNode.getTextContent();
			else if ("tk:Xml".equals(childNode.getNodeName()))
				this.xml = childNode.getTextContent();
			else if ("tk:DataRight".equals(childNode.getNodeName()))
				this.dataRight = DataRightConfigItem.parse(childNode);
		}
	}

	public final AbstractXmlTableResolver newTableResolver(RegsCollection regs,
			BaseDataSet data) {
		AbstractXmlTableResolver result = null;
		switch (createType) {
		case RegName:
			result = (AbstractXmlTableResolver) ResolverUtil.newDataAdapter(
					regs, this, data);
			break;
		case Xml:
			result = ResolverUtil.newTabResolver(this, data);
			break;
		default:
			//Debug.Assert(false, "猪头，这个错误不应该的。因为ResolverCreateType没有你所要的类型。");
			break;
		}
		//Debug.Assert(result is Xml2TableResolver, "猪头，Toolkit2.5需要从Xml2TableResolver继承的TableResolver");
		return result;
	}

	public static ResolverConfigItem parse(Node node) {
		if (node == null)
			return null;
		ResolverConfigItem result = new ResolverConfigItem();
		result.readXml(node);
		return result;
	}
}