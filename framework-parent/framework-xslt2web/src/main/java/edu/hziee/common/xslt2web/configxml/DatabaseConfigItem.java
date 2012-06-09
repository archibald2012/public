package edu.hziee.common.xslt2web.configxml;

import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.hziee.common.xslt2web.sysutil.XmlUtil;

public class DatabaseConfigItem {
	private Node configNode;

	private String sqlProvider;

	private String dbProvider;

	private int iDStep;

	private ConnectionStringConfigItem connectionString;

	private ArrayList<EnvironmentConfigItem> environment;

	public DatabaseConfigItem() {
		this.environment = new ArrayList<EnvironmentConfigItem>();
	}

	public final Node getConfigNode() {
		return configNode;
	}

	public final String getSqlProvider() {
		return this.sqlProvider;
	}

	public final String getDbProvider() {
		return this.dbProvider;
	}

	public final int getIDStep() {
		return this.iDStep;
	}

	public final ConnectionStringConfigItem getConnectionString() {
		return this.connectionString;
	}

	public final ArrayList<EnvironmentConfigItem> getEnvironment() {
		return this.environment;
	}

	public void readXml(Node node) {
		this.configNode = node;

		NodeList childNodes = node.getChildNodes();
		int count = childNodes.getLength();
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if ("SqlProvider".equals(childNode.getNodeName()))
				this.sqlProvider = childNode.getTextContent();
			else if ("DbProvider".equals(childNode.getNodeName()))
				this.dbProvider = childNode.getTextContent();
			else if ("IDStep".equals(childNode.getNodeName()))
				this.iDStep = XmlUtil.getNodeIntValue(childNode, 1);
			else if ("ConnectionString".equals(childNode.getNodeName()))
				this.connectionString = ConnectionStringConfigItem
						.parse(childNode);
			else if ("Environment".equals(childNode.getNodeName()))
				this.environment.add(EnvironmentConfigItem.parse(childNode));
		}
	}

	public static DatabaseConfigItem parse(Node node) {
		if (node == null)
			return null;
		DatabaseConfigItem result = new DatabaseConfigItem();
		result.readXml(node);
		return result;
	}
}