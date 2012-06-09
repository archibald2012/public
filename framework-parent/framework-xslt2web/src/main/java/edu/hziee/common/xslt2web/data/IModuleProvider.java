package edu.hziee.common.xslt2web.data;

import javax.servlet.http.HttpServletRequest;

import edu.hziee.common.xslt2web.sys.PageStyle;

public interface IModuleProvider {
	String getAlertString(PageStyle style, String operation,
			HttpServletRequest request, DataSet postDataSet);

	String getDataXslt(PageStyle style, String operation);

	String getDefaultXsltTemplate(PageStyle style, String operation);

	Object getSubFunctionKey(PageStyle style, String operation);

	boolean getSupportDoubleTransform(PageStyle style, String operation);

	String getTabSheetCondition(String tab);

	String getXsltFile(boolean isIe, PageStyle style, String operation);
}
