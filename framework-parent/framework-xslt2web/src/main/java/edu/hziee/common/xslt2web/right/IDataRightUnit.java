package edu.hziee.common.xslt2web.right;

import edu.hziee.common.xslt2web.sys.IParamBuilder;

public interface IDataRightUnit {
	Object getType();

	void checkReadOnly(Object userId);

	void checkReadWrite(Object ownerId);

	void checkPublic(Object ownerId);

	void checkDelete(Object ownerId);

	IParamBuilder getPublicSql(String fieldName, Object userID);

	void initialize(Object data);
}
