package edu.hziee.common.xslt2web.configxml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AppConfigXml extends XmlConfig {
	private EncodingConfigItem encoding;
	private ApplicationConfigItem applicationItem;
	private DatabaseConfigItem databaseItem;
	private DebugConfigItem debugItem;
	private OtherConfigItem otherItem;

	public AppConfigXml() {
		super();
	}

	@Override
	protected String getFileName(String xmlFile) {
		return xmlFile;
	}

	public final EncodingConfigItem getEncoding() {
		return this.encoding;
	}

	public final ApplicationConfigItem getApplicationItem() {
		return applicationItem;
	}

	public final DatabaseConfigItem getDatabaseItem() {
		return databaseItem;
	}

	public final DebugConfigItem getDebugItem() {
		return debugItem;
	}

	public final OtherConfigItem getOtherItem() {
		return otherItem;
	}

	public final void loadFile(String xmlFile) {
		loadXmlFile(xmlFile);

		NodeList childNodes = getToolkitNode().getChildNodes();
		int count = childNodes.getLength();
		for (int i = 0; i < count; ++i) {
			Node childNode = childNodes.item(i);
			if ("Encoding".equals(childNode.getNodeName()))
				this.encoding = EncodingConfigItem.parse(childNode);
			else if ("Application".equals(childNode.getNodeName()))
				this.applicationItem = ApplicationConfigItem.parse(childNode);
			else if ("Database".equals(childNode.getNodeName()))
				this.databaseItem = DatabaseConfigItem.parse(childNode);
			else if ("Debug".equals(childNode.getNodeName()))
				this.debugItem = DebugConfigItem.parse(childNode);
			else if ("Other".equals(childNode.getNodeName()))
				this.otherItem = OtherConfigItem.parse(childNode);
		}
	}
}
