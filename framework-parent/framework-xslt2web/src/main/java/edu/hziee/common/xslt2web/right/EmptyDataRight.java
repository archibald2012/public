package edu.hziee.common.xslt2web.right;

import java.util.Observable;

import edu.hziee.common.xslt2web.sys.IParamBuilder;

public class EmptyDataRight implements IDataRight {
	public EmptyDataRight() {
	}

	public void checkDelete(Object type, Object ownerId) {
	}

	public void checkPublic(Object type, Object ownerId) {
	}

	public void checkReadOnly(Object type, Object userId) {
	}

	public void checkReadWrite(Object type, Object ownerId) {
	}

	public IParamBuilder getPublicSql(Object type, String fieldName, Object userID) {
		return null;
	}

	public void update(Observable o, Object arg) {
	}

}
