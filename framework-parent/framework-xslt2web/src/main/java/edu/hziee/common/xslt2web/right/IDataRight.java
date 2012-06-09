package edu.hziee.common.xslt2web.right;

import java.util.Observer;

import edu.hziee.common.xslt2web.sys.IParamBuilder;

public interface IDataRight extends Observer {
	void checkReadOnly(Object type, Object userId);

	void checkReadWrite(Object type, Object ownerId);

	void checkPublic(Object type, Object ownerId);

	void checkDelete(Object type, Object ownerId);

	IParamBuilder getPublicSql(Object type, String fieldName, Object userID);
}
