package edu.hziee.common.xslt2web.configxml;

import java.util.EnumSet;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.hziee.common.xslt2web.data.IFieldInfo;
import edu.hziee.common.xslt2web.data.SQLFlag;
import edu.hziee.common.xslt2web.data.TypeCode;
import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class FieldConfigItem implements IFieldInfo {
	public final static EnumSet<HtmlCtrlType> EMPTY_CHECK = EnumSet
			.of(HtmlCtrlType.Text, HtmlCtrlType.Password,
					HtmlCtrlType.TextArea, HtmlCtrlType.CheckBox,
					HtmlCtrlType.EasySearch, HtmlCtrlType.Upload);

	public final static EnumSet<HtmlCtrlType> TEXT = EnumSet.of(
			HtmlCtrlType.Text, HtmlCtrlType.Password, HtmlCtrlType.TextArea);

	public final static EnumSet<HtmlCtrlType> DETAIL = EnumSet.of(
			HtmlCtrlType.Detail, HtmlCtrlType.DetailHidden,
			HtmlCtrlType.DetailHTML, HtmlCtrlType.DetailMultiSelect,
			HtmlCtrlType.DetailTextArea, HtmlCtrlType.DetailUpload);

	private Node configNode;

	private boolean isKey;

	private FieldDataType dataType;

	private boolean isEmpty;

	private FieldKindType kind;

	private boolean isAutoInc;

	private String fieldName;

	private String displayName;

	private int length;

	private String codeTable;

	private String easySearch = "";

	private ExtensionConfigItem extension;

	private HtmlCtrlConfigItem htmlCtrl;

	private MarcoConfigItem defaultValue;

	private UpdatingConfigItem updating;

	private UploadConfigItem upload;

	private EnumSet<SQLFlag> sqlFlag;

	private TypeCode typeCode;

	public FieldConfigItem() {
	}

	private TypeCode convertTypeCode(FieldDataType dataType) {
		switch (dataType) {
		case Binary:
			return TypeCode.Byte;
		case Blob:
			return TypeCode.Blob;
		case Date:
		case DateTime:
			return TypeCode.DateTime;
		case Double:
			return TypeCode.Double;
		case Int:
			return TypeCode.Int32;
		case Money:
			return TypeCode.Decimal;
		case String:
			return TypeCode.String;
		case Text:
			return TypeCode.Clob;
		case Xml:
			return TypeCode.String;
		}
		return TypeCode.String;
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final boolean isKey() {
		return this.isKey;
	}

	public final String getColumnName() {
		return fieldName;
	}

	public final TypeCode getDataType() {
		return typeCode;
	}

	public EnumSet<SQLFlag> getSQLFlag() {
		return sqlFlag;
	}

	public void setSQLFlag(EnumSet<SQLFlag> flag) {
		this.sqlFlag = flag;
	}

	public final FieldDataType getFieldDataType() {
		return this.dataType;
	}

	public final boolean isEmpty() {
		return this.isEmpty;
	}

	public final FieldKindType getKind() {
		return this.kind;
	}

	public final boolean isAutoInc() {
		return this.isAutoInc;
	}

	public final String getFieldName() {
		return this.fieldName;
	}

	public final String getDisplayName() {
		return this.displayName;
	}

	public final int getLength() {
		return this.length;
	}

	public final String getCodeTable() {
		return this.codeTable;
	}

	public final String getEasySearch() {
		return this.easySearch;
	}

	public final ExtensionConfigItem getExtension() {
		return this.extension;
	}

	public final HtmlCtrlConfigItem getHtmlCtrl() {
		return this.htmlCtrl;
	}

	public final MarcoConfigItem getDefaultValue() {
		return this.defaultValue;
	}

	public final UpdatingConfigItem getUpdating() {
		return this.updating;
	}

	public final UploadConfigItem getUpload() {
		return this.upload;
	}

	public final boolean isSpan() {
		return this.extension == null ? false : this.extension.isSpan();
	}

	public void readXml(Node node) {
		this.configNode = node;

		NamedNodeMap attributes = node.getAttributes();
		this.isKey = XmlUtil.getNodeBoolValue(attributes.getNamedItem("IsKey"),
				false);
		String dataTypeValue = XmlUtil.getNodeContent(attributes
				.getNamedItem("DataType"), "String");
		dataTypeValue = Character.toUpperCase(dataTypeValue.charAt(0))
				+ dataTypeValue.substring(1);
		this.dataType = (FieldDataType) Enum.valueOf(FieldDataType.class,
				dataTypeValue);
		this.typeCode = convertTypeCode(this.dataType);
		this.isEmpty = XmlUtil.getNodeBoolValue(attributes
				.getNamedItem("IsEmpty"), false);
		String kindValue = XmlUtil.getNodeContent(attributes
				.getNamedItem("Kind"), "Data");
		this.kind = (FieldKindType) Enum
				.valueOf(FieldKindType.class, kindValue);
		this.isAutoInc = XmlUtil.getNodeBoolValue(attributes
				.getNamedItem("IsAutoInc"), false);

		NodeList childNodes = node.getChildNodes();
		int count = childNodes.getLength();
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if ("tk:FieldName".equals(childNode.getNodeName()))
				this.fieldName = childNode.getTextContent();
			else if ("tk:DisplayName".equals(childNode.getNodeName()))
				this.displayName = childNode.getTextContent();
			else if ("tk:Length".equals(childNode.getNodeName()))
				this.length = XmlUtil.getNodeIntValue(childNode, 0);
			else if ("tk:CodeTable".equals(childNode.getNodeName()))
				this.codeTable = XmlUtil.getNodeContent(childNode);
			else if ("tk:EasySearch".equals(childNode.getNodeName())) {
				attributes = childNode.getAttributes();
				easySearch = attributes.getNamedItem("RegName")
						.getTextContent();
			} else if ("tk:Extension".equals(childNode.getNodeName()))
				this.extension = ExtensionConfigItem.parse(childNode);
			else if ("tk:HtmlCtrl".equals(childNode.getNodeName()))
				this.htmlCtrl = HtmlCtrlConfigItem.parse(childNode);
			else if ("tk:DefaultValue".equals(childNode.getNodeName()))
				this.defaultValue = MarcoConfigItem.parse(childNode);
			else if ("tk:Updating".equals(childNode.getNodeName()))
				this.updating = UpdatingConfigItem.parse(childNode);
			else if ("tk:Upload".equals(childNode.getNodeName()))
				this.upload = UploadConfigItem.parse(childNode);
		}
	}

	public static FieldConfigItem parse(Node node) {
		if (node == null)
			return null;
		FieldConfigItem result = new FieldConfigItem();
		result.readXml(node);
		return result;
	}
}