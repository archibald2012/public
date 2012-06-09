package edu.hziee.common.xslt2web.configxml;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.hziee.common.xslt2web.data.IFieldInfo;
import edu.hziee.common.xslt2web.sysutil.XPathUtil;
import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class TableConfigItem implements ITableConfigXml {
	private Node configNode;
	private String tableName;
	private String tableDesc;
	private TableType tableType;
	private ArrayList<FieldConfigItem> field;
	private String keyFields = null;
	private FieldConfigItem[] fields;

	private String getFields;
	private ArrayList<FieldConfigItem> keyItems;
	private HashMap<String, FieldConfigItem> autoIncItems;
	private HashMap<String, FieldConfigItem> uploads;
	private ArrayList<FieldConfigItem> virtualItems;
	private boolean containBlob;

	public TableConfigItem() {
		this.field = new ArrayList<FieldConfigItem>();
		virtualItems = new ArrayList<FieldConfigItem>();
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public IFieldInfo[] getFields() {
		return fields;
	}

	public String getKeyFields() {
		return keyFields;
	}

	public final String getTableName() {
		return this.tableName;
	}

	public final String getTableDesc() {
		return this.tableDesc;
	}

	public final TableType getTableType() {
		return this.tableType;
	}

	public final ArrayList<FieldConfigItem> getField() {
		return this.field;
	}

	public final String getGetFields() {
		return getFields;
	}

	public final HashMap<String, FieldConfigItem> getAutoIncItems() {
		return autoIncItems;
	}

	public final HashMap<String, FieldConfigItem> getUploads() {
		return uploads;
	}

	public final ArrayList<FieldConfigItem> getVirtualItems() {
		return virtualItems;
	}

	public final boolean isContainBlob() {
		return containBlob;
	}

	public void readXml(Node node) {
		this.configNode = node;

		NamedNodeMap attributes = node.getAttributes();
		this.tableName = attributes.getNamedItem("TableName").getTextContent();
		this.tableDesc = attributes.getNamedItem("TableDesc").getTextContent();
		String tableTypeValue = XmlUtil.getNodeContent(attributes
				.getNamedItem("TableType"), "Table");
		this.tableType = (TableType) Enum.valueOf(TableType.class,
				tableTypeValue);

		NodeList childNodes = node.getChildNodes();
		int count = childNodes.getLength();
		int keyCount = 0, autoIncCount = 0;
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if ("tk:Field".equals(childNode.getNodeName())) {
				FieldConfigItem item = readFieldNodes(childNode);
				if (item.isKey())
					++keyCount;
				if (item.isAutoInc())
					++autoIncCount;
				if (item.getFieldDataType() == FieldDataType.Text
						|| item.getFieldDataType() == FieldDataType.Blob)
					containBlob = true;
			}
		}
		initInternal(keyCount, autoIncCount);
		fields = this.field.toArray(new FieldConfigItem[field.size()]);
	}

	private void initInternal(int keyCount, int autoIncCount) {
		StringBuilder keyFields = new StringBuilder();
		StringBuilder getFields = new StringBuilder();

		if (keyCount > 0)
			keyItems = new ArrayList<FieldConfigItem>();
		if (autoIncCount > 0)
			autoIncItems = new HashMap<String, FieldConfigItem>(autoIncCount);
		for (int i = 0; i < field.size(); i++) {
			FieldConfigItem item = field.get(i);
			if (keyCount > 0 && item.isKey())
				keyItems.add(item);
			if (autoIncCount > 0 && item.isAutoInc())
				autoIncItems.put(item.getFieldName(), item);
			//newItems[i] = item;

			if (item.getKind() != FieldKindType.Data)
				continue;

			if (item.getFieldDataType() != FieldDataType.Blob) {
				if (getFields.length() > 0)
					getFields.append(",");
				getFields.append(item.getFieldName());
			}
			if (item.isKey()) {
				if (keyFields.length() > 0)
					keyFields.append(",");
				keyFields.append(item.getFieldName());
			}
			if (item.getKind() != FieldKindType.Data)
				virtualItems.add(item);
		}
		this.keyFields = keyFields.toString();
		this.getFields = getFields.toString();
	}

	private FieldConfigItem readFieldNodes(Node childNode) {
		FieldConfigItem item = FieldConfigItem.parse(childNode);
		this.field.add(item);
		//		if (item.isKey()) {
		//			if (StringUtil.isEmpty(keyFields))
		//				keyFields = item.getColumnName();
		//			else
		//				keyFields += ", " + item.getColumnName();
		//		}
		return item;
	}

	public void parseXml(Node node, String tableName) {
		Node tableNode = XPathUtil.executeSingleNode(node, String.format(
				"/Toolkit/tk:Table[@SrcTableName='%s']", tableName));
		NodeList fieldNodes = XPathUtil.executeNodes(tableNode, "*/tk:Field");
		int count = fieldNodes.getLength();
		for (int i = 0; i < count; i++) {
			Node childNode = fieldNodes.item(i);
			readFieldNodes(childNode);
		}
		initInternal(0, 0);
	}

	public final boolean isKey(String colName) {
		if (keyItems.size() == 1)
			return keyFields.equals(colName);
		else {
			for (FieldConfigItem item : keyItems) {
				if (item.getFieldName().equals(colName))
					return true;
			}
			return false;
		}
	}

	public static TableConfigItem parse(Node node) {
		if (node == null)
			return null;
		TableConfigItem result = new TableConfigItem();
		result.readXml(node);
		return result;
	}

	public static TableConfigItem parseTempXml(Node node, String tableName) {
		TableConfigItem result = new TableConfigItem();
		result.parseXml(node, tableName);
		return result;
	}
}