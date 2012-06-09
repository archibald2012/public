package edu.hziee.common.xslt2web.easysearch;

import edu.hziee.common.xslt2web.configxml.EasySearchConfigItem;

public class XmlEasySearch extends EasySearch {
	private EasySearchConfigItem configItem;
	private EasySearchAttribute customAttribute;

	public XmlEasySearch(EasySearchConfigItem config) {
		configItem = config;
		customAttribute = EasySearchAttribute.create(config);
		EasySearchUtil.setEasySearchProperty(this, config);
	}

	public final EasySearchConfigItem getConfigItem() {
		return configItem;
	}

	public EasySearchAttribute getCustomAttribute() {
		return customAttribute;
	}

}
