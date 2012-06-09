package edu.hziee.common.xslt2web.data;

import java.util.EnumSet;

import org.w3c.dom.Document;

import edu.hziee.common.xslt2web.configxml.ModuleXml;
import edu.hziee.common.xslt2web.sys.PageStyle;

@XmlDataSetAnnotation(regName = WebAllRowXmlDataSet._REG_NAME, description = "编辑单表全部记录", author = "YJC", createDate = "2008-06-30")
public class WebAllRowXmlDataSet extends WebAllRowDataSet {
	static final String _REG_NAME = "AllRow";
	private ModuleXml module;

	public WebAllRowXmlDataSet(ModuleXml module) {
		super();
		this.module = module;
		DataSetOperUtil.setDataSetProperty(this, module);
		setModuleProvider(new ModuleXmlProvider(module));
		setDisablePage(EnumSet.of(DisableFunction.Delete,
				DisableFunction.Insert, DisableFunction.Detail));
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
