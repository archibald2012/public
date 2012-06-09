package edu.hziee.common.xslt2web.configxml;

import edu.hziee.common.xslt2web.sys.AbstractXmlConfig;
import edu.hziee.common.xslt2web.sys.IXmlConfigItem;

public class EasySearchXml extends AbstractXmlConfig {
	@Override
	public IXmlConfigItem createConfigItem() {
		return new EasySearchConfigItem();
	}

	@Override
	public String getBaseNodeName() {
		return "tk:EasySearch";
	}
}