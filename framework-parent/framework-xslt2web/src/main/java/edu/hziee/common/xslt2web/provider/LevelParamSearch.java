package edu.hziee.common.xslt2web.provider;

import edu.hziee.common.xslt2web.data.TypeCode;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sys.IParamSearch;
import edu.hziee.common.xslt2web.sys.SQLParamBuilder;
import edu.hziee.common.xslt2web.sys.SearchSQLType;

public class LevelParamSearch implements IParamSearch {
	private BaseLevelSQL levelSQL;

	public LevelParamSearch(BaseLevelSQL levelSQL) {
		super();
		this.levelSQL = levelSQL;
	}

	public String getCondition(String fieldName, String fieldValue) {
		return null;
	}

	public IParamBuilder getParamCondition(TypeCode type, String fieldName,
			String fieldValue) {
		if (fieldValue.length() == levelSQL.getLength())
			return SQLParamBuilder.getEqualSQL(type, fieldName, fieldValue);
		else
			return levelSQL.getQuerySQL(type, fieldName, fieldValue);
	}

	public SearchSQLType getType() {
		return SearchSQLType.Param;
	}
}
