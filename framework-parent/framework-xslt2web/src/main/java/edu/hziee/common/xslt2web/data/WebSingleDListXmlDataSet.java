package edu.hziee.common.xslt2web.data;

import java.util.ArrayList;

import org.w3c.dom.Document;

import edu.hziee.common.xslt2web.configxml.ModuleXml;
import edu.hziee.common.xslt2web.configxml.RelationConfigItem;
import edu.hziee.common.xslt2web.configxml.ResolverConfigItem;
import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sys.RegsCollection;

@XmlDataSetAnnotation(regName = WebSingleDListXmlDataSet._REG_NAME, description = "单表在Detail时支持DetailList子表", author = "YJC", createDate = "2008-06-30")
public class WebSingleDListXmlDataSet extends WebSingleDListDataSet {
	static final String _REG_NAME = "SingleDList";
	private ModuleXml module;

	public WebSingleDListXmlDataSet(ModuleXml module) {
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
		ArrayList<ResolverConfigItem> items = module.getModule().getResolvers()
				.getResolver();
		RegsCollection regs = this.getAppGlobal().getRegsCollection();
		setMainResolver(items.get(0).newTableResolver(regs, this));
		for (int i = 1; i < items.size(); ++i) {
			ResolverConfigItem item = items.get(i);
			getDetailResolvers().add(item.getRegName(),
					item.newTableResolver(regs, this));
		}

		for (RelationConfigItem item : module.getModule().getRelations()
				.getRelation()) {
			TableRelation relation = item.newRelation(getMainResolver(),
					getDetailResolvers(), this);
			if (relation != null)
				getRelations().add(relation);
		}
	}

	@Override
	public String getDefaultPage(boolean isPost, PageStyle style,
			String operation, String retURL) {
		return module.getDefaultPage(getMainResolver(), isPost, style,
				getSaveMethod(), retURL);
	}

}
