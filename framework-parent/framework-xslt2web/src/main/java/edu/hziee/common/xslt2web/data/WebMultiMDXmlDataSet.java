package edu.hziee.common.xslt2web.data;

import java.util.ArrayList;

import org.w3c.dom.Document;

import edu.hziee.common.xslt2web.configxml.ModuleXml;
import edu.hziee.common.xslt2web.configxml.RelationConfigItem;
import edu.hziee.common.xslt2web.configxml.ResolverConfigItem;
import edu.hziee.common.xslt2web.sys.PageStyle;
import edu.hziee.common.xslt2web.sys.RegsCollection;

@XmlDataSetAnnotation(regName = WebMultiMDXmlDataSet._REG_NAME, description = "多主表，多从表", author = "YJC", createDate = "2008-07-03")
public class WebMultiMDXmlDataSet extends WebMultiMDDataSet {
	static final String _REG_NAME = "MultipleMasterDetail";
	private ModuleXml module;

	public WebMultiMDXmlDataSet(ModuleXml module) {
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
				if (item.isDetailList())
					getDetailResolvers().add(item.getRegName(), resolver);
				else
					getMasterResolvers().add(item.getRegName(), resolver);
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
			if (getDetailResolvers().containsAdapter(item.getDetailResolver())) {
				TableRelation relation = item.newRelation(getMasterResolvers(),
						getDetailResolvers(), this);
				if (relation != null)
					getDetailRelations().add(relation);
			} else {
				TableRelation relation = item.newRelation(getMasterResolvers(),
						this);
				if (relation != null)
					getMasterRelations().add(relation);
			}
		}
	}

	@Override
	public String getDefaultPage(boolean isPost, PageStyle style,
			String operation, String retURL) {
		return module.getDefaultPage(getMainResolver(), isPost, style,
				getSaveMethod(), retURL);
	}
}
