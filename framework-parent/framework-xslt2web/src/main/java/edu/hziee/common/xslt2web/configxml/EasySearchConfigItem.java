package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.hziee.common.xslt2web.easysearch.EasySearch;
import edu.hziee.common.xslt2web.easysearch.EasySearchType;
import edu.hziee.common.xslt2web.sys.IXmlConfigItem;
import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class EasySearchConfigItem implements IXmlConfigItem {
	private Node configNode;
	private String regName;
	private boolean useCache;
	private String description;
	private EasySearchType type;
	private String baseClass;
	private String tableName;
	private String codeField;
	private String nameField;
	private String pYField;
	private String infoField;
	private String otherFields;
	private String defaultOrder;
	private int topCount;
	private String search;
	private LevelConfigItem level;
	private TreeConfigItem tree;
	private MarcoConfigItem sql;
	private DataRightConfigItem dataRight;

	public EasySearchConfigItem() {
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final String getRegName() {
		return this.regName;
	}

	public final boolean isUseCache() {
		return this.useCache;
	}

	public final String getDescription() {
		return this.description;
	}

	public final EasySearchType getType() {
		return this.type;
	}

	public final String getBaseClass() {
		return this.baseClass;
	}

	public final String getTableName() {
		return this.tableName;
	}

	public final String getCodeField() {
		return this.codeField;
	}

	public final String getNameField() {
		return this.nameField;
	}

	public final String getPYField() {
		return this.pYField;
	}

	public final String getInfoField() {
		return this.infoField;
	}

	public final String getOtherFields() {
		return this.otherFields;
	}

	public final String getDefaultOrder() {
		return this.defaultOrder;
	}

	public final int getTopCount() {
		return this.topCount;
	}

	public final String getSearch() {
		return this.search;
	}

	public final LevelConfigItem getLevel() {
		return this.level;
	}

	public final TreeConfigItem getTree() {
		return this.tree;
	}

	public final MarcoConfigItem getSQL() {
		return this.sql;
	}

	public final DataRightConfigItem getDataRight() {
		return this.dataRight;
	}

	public void readXml(Node node) {
		this.configNode = node;

		NamedNodeMap attributes = node.getAttributes();
		NodeList childNodes = node.getChildNodes();
		int count = childNodes.getLength();
		this.codeField = this.nameField = this.pYField = this.infoField = this.otherFields = this.defaultOrder = this.search = "";
		this.topCount = 15;

		this.baseClass = XmlUtil.getNodeContent(attributes
				.getNamedItem("BaseClass"), "");
        
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if ("TableName".equals(childNode.getNodeName())) {
				this.tableName = childNode.getTextContent();
		        if ("CodeTableEasySearch".equals(this.baseClass))
		            setCodeTable(this.tableName);
			}
			else if ("CodeField".equals(childNode.getNodeName()))
				this.codeField = XmlUtil.getNodeContent(childNode, this.codeField);
			else if ("NameField".equals(childNode.getNodeName()))
				this.nameField = XmlUtil.getNodeContent(childNode, this.nameField);
			else if ("PYField".equals(childNode.getNodeName()))
				this.pYField = XmlUtil.getNodeContent(childNode, this.pYField);
			else if ("InfoField".equals(childNode.getNodeName()))
				this.infoField = XmlUtil.getNodeContent(childNode, this.infoField);
			else if ("OtherFields".equals(childNode.getNodeName()))
				this.otherFields = XmlUtil.getNodeContent(childNode, this.otherFields);
			else if ("DefaultOrder".equals(childNode.getNodeName()))
				this.defaultOrder = XmlUtil.getNodeContent(childNode, this.defaultOrder);
			else if ("TopCount".equals(childNode.getNodeName()))
				this.topCount = XmlUtil.getNodeIntValue(childNode, this.topCount);
			else if ("Search".equals(childNode.getNodeName()))
				this.search = XmlUtil.getNodeContent(childNode);
			else if ("Level".equals(childNode.getNodeName()))
				this.level = LevelConfigItem.parse(childNode);
			else if ("Tree".equals(childNode.getNodeName()))
				this.tree = TreeConfigItem.parse(childNode, this);
			else if ("SQL".equals(childNode.getNodeName()))
				this.sql = MarcoConfigItem.parse(childNode);
			else if ("DataRight".equals(childNode.getNodeName()))
				this.dataRight = DataRightConfigItem.parse(childNode);
		}
		
		this.regName = attributes.getNamedItem("RegName").getTextContent();
        boolean defaultCache = tableName.startsWith("CD_");
		this.useCache = XmlUtil.getNodeBoolValue(attributes
				.getNamedItem("UseCache"), defaultCache);
		this.description = XmlUtil.getNodeContent(attributes
				.getNamedItem("Description"));
		String typeValue = XmlUtil.getNodeContent(attributes
				.getNamedItem("Type"), "Normal");
		this.type = (EasySearchType) Enum.valueOf(EasySearchType.class,
				typeValue);
	}

	void setCodeTable(String tableName) {
		regName = this.tableName = tableName;
		description = String.format("´úÂë±í(%s)", tableName);
		useCache = true;
		type = EasySearchType.Normal;
		baseClass = "CodeTableEasySearch";
		nameField = "CODE_NAME";
		codeField = "CODE_VALUE";
		pYField = "CODE_PY";
		//activeField = "CODE_DEL";
		infoField = "CODE_VALUE";
		otherFields = "";
		defaultOrder = "CODE_SORT, CODE_VALUE";
		topCount = EasySearch.TOP_COUNT;
		sql = null;
		level = null;
		tree = null;
		dataRight = null;
	}

	public static EasySearchConfigItem parse(Node node) {
		if (node == null)
			return null;
		EasySearchConfigItem result = new EasySearchConfigItem();
		result.readXml(node);
		return result;
	}
}