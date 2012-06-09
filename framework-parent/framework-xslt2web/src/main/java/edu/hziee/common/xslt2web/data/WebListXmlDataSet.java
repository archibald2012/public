package edu.hziee.common.xslt2web.data;

import org.w3c.dom.Document;

import edu.hziee.common.xslt2web.configxml.ModuleXml;
import edu.hziee.common.xslt2web.sys.PageStyle;

@XmlDataSetAnnotation(regName = WebListXmlDataSet._REG_NAME, description = "单表仅有List", author = "YJC", createDate = "2008-07-01")
public class WebListXmlDataSet extends WebListDataSet {
	static final String _REG_NAME = "List";
	private ModuleXml module;

	public WebListXmlDataSet(ModuleXml module) {
		super();
		this.module = module;
		DataSetOperUtil.setDataSetProperty(this, module);
		setModuleProvider(new ModuleXmlProvider(module));
	}

	@Override
	protected Document getModuleXml() {
		return module.getDocument();
	}

	protected void setResolvers() {
		setMainResolver(module
				.getModule()
				.getResolvers()
				.getResolver()
				.get(0)
				.newTableResolver(this.getAppGlobal().getRegsCollection(), this));
	}

	@Override
	public String getDefaultPage(boolean isPost, PageStyle style,
			String operation, String retURL) {
		return module.getDefaultPage(getMainResolver(), isPost, style, "",
				retURL);
	}
}
