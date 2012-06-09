package edu.hziee.common.xslt2web.data;

import edu.hziee.common.xslt2web.configxml.DataXml;

public class XmlTableResolver extends AbstractXmlTableResolver {
	private String xmlFile;

	public XmlTableResolver(BaseDataSet hostDataSet) {
		super(hostDataSet);
	}

	public final String getXmlFile() {
		return xmlFile;
	}

	public final void setXmlFile(String xmlFile) {
		this.xmlFile = xmlFile;
		DataXml xml = new DataXml();
		xml.loadFile(xmlFile);
		setDataXml(xml);
	}
}
