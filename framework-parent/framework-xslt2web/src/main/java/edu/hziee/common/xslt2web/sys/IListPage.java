package edu.hziee.common.xslt2web.sys;

import java.util.HashMap;

import edu.hziee.common.xslt2web.data.DataSet;

public interface IListPage extends IXmlHttpPostPage {
	HashMap<String, String> getIgnoreParams();

	String getQueryCondition(DataSet postDataSet);
}
