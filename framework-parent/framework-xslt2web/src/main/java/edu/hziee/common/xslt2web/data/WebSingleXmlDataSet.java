package edu.hziee.common.xslt2web.data;

import org.w3c.dom.Document;

import edu.hziee.common.xslt2web.configxml.ModuleXml;
import edu.hziee.common.xslt2web.sys.PageStyle;

@XmlDataSetAnnotation(regName = WebSingleXmlDataSet._REG_NAME, description = "µ¥±í", author = "YJC", createDate = "2008-05-20")
public class WebSingleXmlDataSet extends WebSingleDataSet {
	static final String _REG_NAME = "Single";
	private ModuleXml module;

	public WebSingleXmlDataSet(ModuleXml module) {
		super();
		this.module = module;
		DataSetOperUtil.setDataSetProperty(this, module);
		setModuleProvider(new ModuleXmlProvider(module));
	}

	protected final ModuleXml getModule() {
		return module;
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
		return module.getDefaultPage(getMainResolver(), isPost, style,
				getSaveMethod(), retURL);
	}
}
