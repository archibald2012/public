package edu.hziee.common.xslt2web.provider;

import javax.servlet.http.HttpServletRequest;

import edu.hziee.common.xslt2web.sys.ParamExpression;
import edu.hziee.common.xslt2web.sys.ParamExpressionAnnotation;

@ParamExpressionAnnotation(regName = QueryStringParamExpression.REG_NAME, regChar = QueryStringParamExpression.REG_CHAR, sqlInject = false, author = "YJC", createDate = "2008-06-01", description = "通过Request.QueryString获得数据(#)")
public class QueryStringParamExpression extends ParamExpression {
	public static final String REG_NAME = "QueryString";
	public static final char REG_CHAR = '#';

	@Override
	public String execute(String param) {
		Object data = getData();
		if (data != null && data instanceof HttpServletRequest)
			return ((HttpServletRequest)data).getParameter(param);
		else
			return "";
	}
}
