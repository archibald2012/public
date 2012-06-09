package edu.hziee.common.xslt2web.easysearch;

import edu.hziee.common.xslt2web.configxml.CodeTableConfigItem;

public class XmlCodeTable extends CodeTable {
	private CodeTableConfigItem configItem;
	private CodeTableAttribute customAttribute;

	public XmlCodeTable(CodeTableConfigItem config) {
		super();
		configItem = config;
		customAttribute = CodeTableAttribute.create(config);
		CodeTableUtil.setCodeTableProperty(this, config);
	}

	public final CodeTableConfigItem getConfigItem() {
		return configItem;
	}

	@Override
	public CodeTableAttribute getAttribute() {
		return customAttribute;
	}

}
