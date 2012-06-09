package edu.hziee.common.xslt2web.data;

import org.w3c.dom.Document;

import edu.hziee.common.xslt2web.configxml.ModuleXml;
import edu.hziee.common.xslt2web.sys.PageStyle;

@XmlDataSetAnnotation(regName = WebConfigXmlDataSet._REG_NAME, description = "配置表，表中只有一条记录用于记录参数", author = "YJC", createDate = "2008-06-30")
public class WebConfigXmlDataSet extends WebConfigDataSet {
	static final String _REG_NAME = "Config";
	private ModuleXml module;

	public WebConfigXmlDataSet(ModuleXml module) {
		super();
		this.module = module;
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
		String url = module.getDefaultPage(getMainResolver(), isPost, style,
				getSaveMethod(), retURL);
		if ("".equals(url))
			return String.format("../toolkit/webdetailxmlpage?Source=%s&ID=-1",
					module.getXmlFile());
		else
			return url;
	}

}
