package edu.hziee.common.xslt2web.sys;

import edu.hziee.common.xslt2web.data.BaseDataSet;
import edu.hziee.common.xslt2web.provider.LoginOrgIDExpression;
import edu.hziee.common.xslt2web.provider.LoginUserIDExpression;
import edu.hziee.common.xslt2web.provider.QueryStringParamExpression;
import edu.hziee.common.xslt2web.provider.UniIDParamExpression;

public class WebDataListener implements ExpressionDataListener {
	private IWebData webData;
	private BaseDataSet dataSet;

	public WebDataListener(IWebData webData, BaseDataSet dataSet) {
		super();
		this.webData = webData;
		this.dataSet = dataSet;
	}

	public void getData(ExpressionDataEventArgs e) {
		if (LoginOrgIDExpression.REG_NAME.equals(e.getRegName())
				|| LoginUserIDExpression.REG_NAME.equals(e.getRegName()))
			e.setData(webData.getSessionGlobal().getInfo());
		else if (UniIDParamExpression.REG_NAME.equals(e.getRegName()))
			e.setData(dataSet.getConnection());
		else if (QueryStringParamExpression.REG_NAME.equals(e.getRegName()))
			e.setData(webData.getRequest());
	}
}
