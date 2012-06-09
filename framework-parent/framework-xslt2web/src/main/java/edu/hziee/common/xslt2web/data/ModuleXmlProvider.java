package edu.hziee.common.xslt2web.data;

import javax.servlet.http.HttpServletRequest;

import edu.hziee.common.xslt2web.configxml.ModuleXml;
import edu.hziee.common.xslt2web.sys.PageStyle;

public class ModuleXmlProvider implements IModuleProvider {
	private ModuleXml module;
	
	public ModuleXmlProvider(ModuleXml module) {
		super();
		this.module = module;
	}

	public String getAlertString(PageStyle style, String operation,
			HttpServletRequest request, DataSet postDataSet) {
		return module.getAlertString(style, operation);
	}

	public String getDataXslt(PageStyle style, String operation) {
		return module.getDataXslt(style, operation);
	}

	public String getDefaultXsltTemplate(PageStyle style, String operation) {
		return module.getDefaultXsltTemplate(style, operation);
	}

	public Object getSubFunctionKey(PageStyle style, String operation) {
		return module.getSubFunctionKey(style, operation);
	}

	public boolean getSupportDoubleTransform(PageStyle style, String operation) {
		return module.getSupportDoubleTransform(style, operation);
	}

	public String getTabSheetCondition(String tab) {
		return module.getTabCondition(tab);
	}

	public String getXsltFile(boolean isIe, PageStyle style, String operation) {
		return module.getXsltFile(style, operation);
	}
}
