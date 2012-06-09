package edu.hziee.common.xslt2web.data;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import edu.hziee.common.xslt2web.sys.QueryParamCondition;

class ListQueryCondition {
	public static final String _SSN_NAME = "List_Query";

	private String guid;
	private QueryParamCondition condition;

	public ListQueryCondition(String guid, QueryParamCondition condition) {
		super();
		this.guid = guid;
		this.condition = condition;
	}

	public final String getGuid() {
		return guid;
	}

	public final void setGuid(String guid) {
		this.guid = guid;
	}

	public final QueryParamCondition getCondition() {
		if (condition == null)
			return new QueryParamCondition();
		else
			return condition.copy();
	}

	public static QueryParamCondition getCondition(HttpServletRequest request,
			String guid) {
		HttpSession session = request.getSession();
		ListQueryCondition condition = (ListQueryCondition) session
				.getAttribute(_SSN_NAME);
		if (condition == null)
			return new QueryParamCondition();
		else {
			if (condition.getGuid().equals(guid))
				return condition.getCondition();
			else
				return new QueryParamCondition();
		}
	}

	public static void setCondition(HttpServletRequest request, String guid,
			QueryParamCondition con) {
		HttpSession session = request.getSession();
		ListQueryCondition condition = (ListQueryCondition) session
				.getAttribute(_SSN_NAME);
		if (condition == null) {
			condition = new ListQueryCondition(guid, con);
			session.setAttribute(_SSN_NAME, condition);
		} else {
			condition.setGuid(guid);
			condition.condition = con;
		}
	}
}
