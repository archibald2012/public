package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.hziee.common.xslt2web.sysutil.FileUtil;
import edu.hziee.common.xslt2web.sysutil.XmlUtil;

import edu.hziee.common.xslt2web.sys.AppSetting;
import edu.hziee.common.xslt2web.sys.XmlConfig;

public class DataXml extends XmlConfig {
	private String version;
	private TableConfigItem table;

	public DataXml() {
		super();
	}

	@Override
	protected String getFileName(String xmlFile) {
		return FileUtil.combin(AppSetting.getCurrent().getDataXmlPath(),
				xmlFile);
	}

	public final String getversion() {
		return this.version;
	}

	public final TableConfigItem getTable() {
		return this.table;
	}

	public final void loadFile(String xmlFile) {
		loadXmlFile(xmlFile);
		Node node = getToolkitNode();

		NamedNodeMap attributes = node.getAttributes();
		this.version = XmlUtil.getNodeContent(attributes
				.getNamedItem("version"), "2.0");

		NodeList childNodes = node.getChildNodes();
		int count = childNodes.getLength();
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if ("tk:Table".equals(childNode.getNodeName()))
				this.table = TableConfigItem.parse(childNode);
		}
	}

	public final void parseXml(Node node, String tableName) {
		this.table = TableConfigItem.parseTempXml(node, tableName);
	}

	public static DataXml parseTempXml(Node node, String tableName) {
		DataXml xml = new DataXml();
		xml.parseXml(node, tableName);
		return xml;
	}
}