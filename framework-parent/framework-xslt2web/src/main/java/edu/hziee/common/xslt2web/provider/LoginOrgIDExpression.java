package edu.hziee.common.xslt2web.provider;

import edu.hziee.common.xslt2web.sys.Expression;
import edu.hziee.common.xslt2web.sys.ExpressionAnnotation;
import edu.hziee.common.xslt2web.sys.UserInfo;

@ExpressionAnnotation(regName = LoginOrgIDExpression.REG_NAME, sqlInject = false, author = "YJC", createDate = "2008-06-01", description = "登陆用户的机构ID")
public class LoginOrgIDExpression extends Expression {
	public static final String REG_NAME = "OrgID";

	@Override
	public String execute() {
		Object data = getData();
		if (data != null && data instanceof UserInfo)
			return ((UserInfo) data).getRoleID().toString();
		else
			return "";
	}

}
