package edu.hziee.common.xslt2web.data;

import java.util.ArrayList;

import org.w3c.dom.Document;

import edu.hziee.common.xslt2web.configxml.ModuleXml;
import edu.hziee.common.xslt2web.configxml.RelationConfigItem;
import edu.hziee.common.xslt2web.configxml.ResolverConfigItem;
import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sys.RegsCollection;

@XmlDataSetAnnotation(regName = WebMultiXmlDataSet._REG_NAME, description = "多表单条记录同时录入", author = "YJC", createDate = "2008-07-03")
public class WebMultiXmlDataSet extends WebMultiDataSet {
	static final String _REG_NAME = "Multiple";
	private ModuleXml module;

	public WebMultiXmlDataSet(ModuleXml module) {
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
		for (ResolverConfigItem item : items) {
			switch (item.getType()) {
			case Table:
				AbstractXmlTableResolver resolver = item.newTableResolver(regs,
						this);
				getUiResolvers().add(item.getRegName(), resolver);
				if (item.isMain())
					setMainResolver(resolver);
				if (item.isList())
					setListView(resolver);
				break;
			case View:
				if (item.isList())
					setListView(ResolverUtil.newTabResolver(item, this));
				break;
			}
		}

		for (RelationConfigItem item : module.getModule().getRelations()
				.getRelation()) {
			TableRelation relation = item.newRelation(getUiResolvers(), this);
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
