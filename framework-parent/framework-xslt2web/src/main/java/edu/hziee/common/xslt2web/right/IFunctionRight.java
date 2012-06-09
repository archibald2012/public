package edu.hziee.common.xslt2web.right;

import java.util.Observer;

public interface IFunctionRight extends Observer {
	String getMenuScript(String userId);

	boolean isFunction(Object key);

	void checkFunction(Object key);

	boolean isSubFunction(Object subKey, Object key);

	void checkSubFunction(Object subKey, Object key);

	boolean isAdmin();

	void checkAdmin();
}
