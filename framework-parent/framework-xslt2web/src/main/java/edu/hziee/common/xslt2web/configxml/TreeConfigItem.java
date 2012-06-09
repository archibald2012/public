package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.hziee.common.xslt2web.data.TypeCode;
import edu.hziee.common.xslt2web.sys.ExpressionDataListener;
import edu.hziee.common.xslt2web.sys.ITreeFieldGroup;
import edu.hziee.common.xslt2web.sys.RegsCollection;
import edu.hziee.common.xslt2web.sys.TreeFieldGroup;
import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class TreeConfigItem implements ITreeFieldGroup {
	private Node configNode;
	private TreeFieldGroup fields;
	private boolean isParentID;
	private boolean onlyLeafSelect;
	private MarcoConfigItem baseValue;
	private String baseSearchField;
	private EasySearchConfigItem parent;
	
	private RegsCollection regs;
	private ExpressionDataListener dataListener;

	public TreeConfigItem(EasySearchConfigItem parent) {
		this.parent = parent;
	}

	public String getRootID() {
		return baseValue.toString(regs, dataListener);
	}

	public String getTableName() {
		return parent.getTableName();
	}

	public TreeFieldGroup getTreeFields() {
		return fields;
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final boolean isParentID() {
		return isParentID;
	}

	public final boolean isOnlyLeafSelect() {
		return this.onlyLeafSelect;
	}

	public final String getBaseSearchField() {
		return baseSearchField;
	}

	public final MarcoConfigItem getBaseValue() {
		return this.baseValue;
	}

	public final void setBaseValueData(RegsCollection regs, ExpressionDataListener dataListener) {
		this.regs = regs;
		this.dataListener = dataListener;
	}
	
	public void readXml(Node node) {
		this.configNode = node;

		NamedNodeMap attributes = node.getAttributes();
		String parentField = attributes.getNamedItem("ParentField")
				.getTextContent();
		String isLeafField = attributes.getNamedItem("IsLeafField")
				.getTextContent();
		String layerField = attributes.getNamedItem("LayerField")
				.getTextContent();
		String dataTypeValue = XmlUtil.getNodeContent(attributes
				.getNamedItem("DataType"), "String");
		dataTypeValue = Character.toUpperCase(dataTypeValue.charAt(0))
				+ dataTypeValue.substring(1);
		FieldDataType dataType = (FieldDataType) Enum.valueOf(
				FieldDataType.class, dataTypeValue);
		TypeCode typeCode = FieldDataType.convertTypeCode(dataType);

		this.fields = new TreeFieldGroup(parent.getCodeField(), parent
				.getNameField(), parentField, layerField, isLeafField, typeCode);
		String baseSearchTypeValue = XmlUtil.getNodeContent(attributes
				.getNamedItem("BaseSearchType"), "ParentID");
		this.isParentID = "ParentID".equals(baseSearchTypeValue);
		this.onlyLeafSelect = XmlUtil.getNodeBoolValue(attributes
				.getNamedItem("OnlyLeafSelect"), false);
		baseSearchField = isParentID ? parentField : parent.getCodeField();

		NodeList childNodes = node.getChildNodes();
		int count = childNodes.getLength();
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if ("BaseValue".equals(childNode.getNodeName()))
				this.baseValue = MarcoConfigItem.parse(childNode);
		}
	}

	public static TreeConfigItem parse(Node node, EasySearchConfigItem parent) {
		if (node == null)
			return null;
		TreeConfigItem result = new TreeConfigItem(parent);
		result.readXml(node);
		return result;
	}
}