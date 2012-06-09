package edu.hziee.common.xslt2web.provider;

import edu.hziee.common.xslt2web.sys.Expression;
import edu.hziee.common.xslt2web.sys.ExpressionAnnotation;
import edu.hziee.common.xslt2web.sys.UserInfo;

@ExpressionAnnotation(regName = LoginUserIDExpression.REG_NAME, sqlInject = false, author = "YJC", createDate = "2008-06-01", description = "µÇÂ¼ÓÃ»§µÄID")
public class LoginUserIDExpression extends Expression {
	public static final String REG_NAME = "UserID";

	@Override
	public String execute() {
		Object data = getData();
		if (data != null && data instanceof UserInfo)
			return ((UserInfo) data).getUserID().toString();
		else
			return "";
	}

}
