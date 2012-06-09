package edu.hziee.common.xslt2web.sys;

import edu.hziee.common.xslt2web.data.TypeCode;

public class DefaultListSearch extends BaseListSearch {
	private static BaseListSearch Instance = new DefaultListSearch();

	private DefaultListSearch() {
		super();
	}

	@Override
	public IParamBuilder getParamCondition(TypeCode type, String fieldName,
			String fieldValue) {
		if (isEqual())
			return SQLParamBuilder.getEqualSQL(type, fieldName, fieldValue);
		else
			return LikeParamBuilder.getLikeSQL(type, fieldName, fieldValue);
	}

	public static final BaseListSearch getInstance() {
		return Instance;
	}
}
