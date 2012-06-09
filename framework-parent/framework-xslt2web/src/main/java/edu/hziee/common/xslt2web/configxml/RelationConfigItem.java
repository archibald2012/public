package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.hziee.common.xslt2web.data.AbstractXmlTableResolver;
import edu.hziee.common.xslt2web.data.BaseDataSet;
import edu.hziee.common.xslt2web.data.RelationType;
import edu.hziee.common.xslt2web.data.TableAdapterCollection;
import edu.hziee.common.xslt2web.data.TableRelation;
import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class RelationConfigItem {
	private Node configNode;
	private RelationType type;
	private String masterResolver;
	private String detailResolver;
	private String masterField;
	private String detailField;
	private MarcoConfigItem filterSQL;
	private String orderBy;

	public RelationConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final RelationType getType() {
		return this.type;
	}

	public final String getMasterResolver() {
		return this.masterResolver;
	}

	public final String getDetailResolver() {
		return this.detailResolver;
	}

	public final String getMasterField() {
		return this.masterField;
	}

	public final String getDetailField() {
		return this.detailField;
	}

	public final MarcoConfigItem getFilterSQL() {
		return this.filterSQL;
	}

	public final String getOrderBy() {
		return this.orderBy;
	}

	public void readXml(Node node) {
		this.configNode = node;

		NamedNodeMap attributes = node.getAttributes();
		String typeValue = XmlUtil.getNodeContent(attributes
				.getNamedItem("Type"), "MasterRelation");
		this.type = (RelationType) Enum.valueOf(RelationType.class, typeValue);

		NodeList childNodes = node.getChildNodes();
		int count = childNodes.getLength();
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if ("tk:MasterResolver".equals(childNode.getNodeName()))
				this.masterResolver = childNode.getTextContent();
			else if ("tk:DetailResolver".equals(childNode.getNodeName()))
				this.detailResolver = childNode.getTextContent();
			else if ("tk:MasterField".equals(childNode.getNodeName()))
				this.masterField = childNode.getTextContent();
			else if ("tk:DetailField".equals(childNode.getNodeName()))
				this.detailField = childNode.getTextContent();
			else if ("tk:FilterSQL".equals(childNode.getNodeName()))
				this.filterSQL = MarcoConfigItem.parse(childNode);
			else if ("tk:OrderBy".equals(childNode.getNodeName()))
				this.orderBy = childNode.getTextContent();
		}
	}

	public final TableRelation newRelation(
			AbstractXmlTableResolver masterResolver,
			AbstractXmlTableResolver detailResolver, BaseDataSet hostDataSet) {
		return new TableRelation(masterResolver, detailResolver, masterField,
				detailField, type, hostDataSet, filterSQL, orderBy);
	}

	public final TableRelation newRelation(
			AbstractXmlTableResolver masterResolver,
			TableAdapterCollection<AbstractXmlTableResolver> detailResolvers,
			BaseDataSet hostDataSet) {
		AbstractXmlTableResolver detailResolver = detailResolvers
				.getAdapter(this.detailResolver);
		if (detailResolver == null)
			return null;
		return newRelation(masterResolver, detailResolver, hostDataSet);
	}

	public final TableRelation newRelation(
			TableAdapterCollection<AbstractXmlTableResolver> resolvers,
			BaseDataSet hostDataSet) {
		AbstractXmlTableResolver masterResolver = resolvers
				.getAdapter(this.masterResolver);
		AbstractXmlTableResolver detailResolver = resolvers
				.getAdapter(this.detailResolver);
		if (masterResolver == null || detailResolver == null)
			return null;
		return newRelation(masterResolver, detailResolver, hostDataSet);
	}

	public final TableRelation newRelation(
			TableAdapterCollection<AbstractXmlTableResolver> masterResolvers,
			TableAdapterCollection<AbstractXmlTableResolver> detailResolvers,
			BaseDataSet hostDataSet) {
		AbstractXmlTableResolver masterResolver = masterResolvers
				.getAdapter(this.masterResolver);
		AbstractXmlTableResolver detailResolver = detailResolvers
				.getAdapter(this.detailResolver);
		if (masterResolver == null || detailResolver == null)
			return null;
		return newRelation(masterResolver, detailResolver, hostDataSet);
	}

	public static RelationConfigItem parse(Node node) {
		if (node == null)
			return null;
		RelationConfigItem result = new RelationConfigItem();
		result.readXml(node);
		return result;
	}
}