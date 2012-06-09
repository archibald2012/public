package edu.hziee.common.xslt2web.data;

import java.util.ArrayList;

import org.w3c.dom.Document;

import edu.hziee.common.xslt2web.configxml.ModuleXml;
import edu.hziee.common.xslt2web.configxml.RelationConfigItem;
import edu.hziee.common.xslt2web.configxml.ResolverConfigItem;
import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sys.RegsCollection;

@XmlDataSetAnnotation(regName = WebMDXmlDataSet._REG_NAME, description = "Ö÷´Ó±í", author = "YJC", createDate = "2008-06-26")
public class WebMDXmlDataSet extends WebMDDataSet {
	static final String _REG_NAME = "MasterDetail";
	private ModuleXml module;

	public WebMDXmlDataSet(ModuleXml module) {
		super();
		this.module = module;
		DataSetOperUtil.setDataSetProperty(this, module);
		setModuleProvider(new ModuleXmlProvider(module));
	}

	@Override
	protected Document getModuleXml() {
		return module.getDocument();
	}

	@Override
	public String getDefaultPage(boolean isPost, PageStyle style,
			String operation, String retURL) {
		return module.getDefaultPage(getMainResolver(), isPost, style,
				getSaveMethod(), retURL);
	}

	protected void setResolvers() {
		ArrayList<ResolverConfigItem> items = module.getModule().getResolvers()
				.getResolver();
		RegsCollection regs = this.getAppGlobal().getRegsCollection();
		setMasterResolver(items.get(0).newTableResolver(regs, this));
		setDetailResolver(items.get(1).newTableResolver(regs, this));
		RelationConfigItem item = module.getModule().getRelations().getRelation().get(0);
		setRelation(item.newRelation(getMasterResolver(), getDetailResolver(), this));
	}
}
