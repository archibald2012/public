package edu.hziee.common.xslt2web.provider;

import edu.hziee.common.xslt2web.data.DbConnection;
import edu.hziee.common.xslt2web.sys.ParamExpression;
import edu.hziee.common.xslt2web.sys.ParamExpressionAnnotation;

@ParamExpressionAnnotation(regName = UniIDParamExpression.REG_NAME, regChar = UniIDParamExpression.REG_CHAR, sqlInject = false, author = "YJC", createDate = "2008-06-01", description = "生成数据表的Unique ID(@)")
public class UniIDParamExpression extends ParamExpression {
	public static final String REG_NAME = "UniqueID";
	public static final char REG_CHAR = '@';

	@Override
	public String execute(String param) {
		Object data = getData();
		if (data != null && data instanceof DbConnection)
			return GlobalProvider.getSqlProvider().getUniID(
					(DbConnection) data, param);
		else
			return "";
	}

}
