package edu.hziee.common.xslt2web.sys;

import javax.servlet.http.HttpServletRequest;
import edu.hziee.common.xslt2web.data.DataSet;

public interface IXmlHttpPostPage extends IXmlPage {
	String getJScript(PageStyle style, String operation);

	String post(PageStyle style, String operation, HttpServletRequest request,
			DataSet postDataSet);

	String getAlertString(PageStyle style, String operation,
			HttpServletRequest request, DataSet postDataSet);
}
