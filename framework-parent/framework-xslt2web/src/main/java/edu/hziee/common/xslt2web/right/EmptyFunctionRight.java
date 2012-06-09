package edu.hziee.common.xslt2web.right;

import java.util.Observable;

import edu.hziee.common.xslt2web.exception.ErrorPageException;

public class EmptyFunctionRight implements IFunctionRight {
	protected class NoRightException extends ErrorPageException {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public NoRightException() {
			super();
		}
	}
	
	public EmptyFunctionRight() {
	}

	public final void checkAdmin() {
		if (isAdmin())
			throw new NoRightException();
	}

	public final void checkFunction(Object key) {
		if (!isFunction(key))
			throw new NoRightException();
	}

	public final void checkSubFunction(Object subKey, Object key) {
		if (!isSubFunction(subKey, key))
			throw new NoRightException();
	}

	public String getMenuScript(String userId) {
		return "";
	}

	public boolean isAdmin() {
		return false;
	}

	public boolean isFunction(Object key) {
		return true;
	}

	public boolean isSubFunction(Object subKey, Object key) {
		return true;
	}

	public void update(Observable o, Object arg) {

	}

}
