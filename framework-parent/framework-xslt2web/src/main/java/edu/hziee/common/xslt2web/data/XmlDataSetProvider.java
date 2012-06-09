package edu.hziee.common.xslt2web.data;

import edu.hziee.common.xslt2web.configxml.ModuleXml;
import edu.hziee.common.xslt2web.exception.ToolkitException;
import edu.hziee.common.xslt2web.sys.RegsCollection;

public class XmlDataSetProvider implements IDataProvider {
	public static final IDataProvider Instance = new XmlDataSetProvider();

	private XmlDataSetProvider() {
	}

	public BaseDataSet createDataSet(RegsCollection regs, String source) {
		XmlDataSetRegCategory category = (XmlDataSetRegCategory) regs
				.get(XmlDataSetRegCategory.REG_NAME);
		if (category == null)
			throw new ToolkitException("");
		ModuleXml module = ModuleXml.loadModuleFile(source);
		BaseDataSet result = category.newArgsInstance(module.getModule().getDataSet(), module);
		if (result == null)
			throw new ToolkitException("");
		return result;
	}

}
