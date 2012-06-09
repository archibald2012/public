package edu.hziee.common.xslt2web.sys;

public class UserInfo {
	private String userName;
	private String userLoginName;
	private String encoding;
	private Object userID;
	private Object roleID;
	private int pageSize;
	private boolean isLogin;
	private Object data1;
	private Object data2;

	public UserInfo() {
		pageSize = 15;
		encoding = "gb2312";
	}

	public final String getUserName() {
		return userName;
	}

	public final void setUserName(String userName) {
		this.userName = userName;
	}

	public final String getUserLoginName() {
		return userLoginName;
	}

	public final void setUserLoginName(String userLoginName) {
		this.userLoginName = userLoginName;
	}

	public final String getEncoding() {
		return encoding;
	}

	public final void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public final Object getUserID() {
		return userID;
	}

	public final void setUserID(Object userID) {
		this.userID = userID;
	}

	public final Object getRoleID() {
		return roleID;
	}

	public final void setRoleID(Object roleID) {
		this.roleID = roleID;
	}

	public final int getPageSize() {
		return pageSize;
	}

	public final void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public final boolean isLogin() {
		return isLogin;
	}

	public final void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

	public final Object getData1() {
		return data1;
	}

	public final void setData1(Object data1) {
		this.data1 = data1;
	}

	public final Object getData2() {
		return data2;
	}

	public final void setData2(Object data2) {
		this.data2 = data2;
	}
}
