package edu.hziee.common.xslt2web.provider;

import edu.hziee.common.xslt2web.data.DbDataParameter;
import edu.hziee.common.xslt2web.data.TypeCode;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sys.IParamSearch;
import edu.hziee.common.xslt2web.sys.SearchSQLType;

public class LevelValueParamSearch implements IParamSearch, IParamBuilder {
	private BaseLevelSQL levelSQL;
	private int level;
	private DbDataParameter[] params;
	private String sql;

	public LevelValueParamSearch(BaseLevelSQL levelSQL, int level) {
		super();
		this.levelSQL = levelSQL;
		this.level = level + 1;
		params = new DbDataParameter[this.level];
	}

	public String getCondition(String fieldName, String fieldValue) {
		return null;
	}

	public IParamBuilder getParamCondition(TypeCode type, String fieldName,
			String fieldValue) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < level; ++i) {
			params[i] = DbDataParameter.createParameter(type);
			String subValue = i == 0 ? "" : fieldValue.substring(0, levelSQL
					.getConfigItem().getSubItem().get(i - 1)
					.getSubTotalLength());
			String likeValue = String.format(levelSQL.getLikeValues(i),
					subValue);
			params[i].setValue(likeValue);
			if (i > 0)
				builder.append(" OR ");
			builder.append(fieldName).append(" LIKE ?");
		}
		this.sql = builder.toString();
		return this;
	}

	public SearchSQLType getType() {
		return SearchSQLType.Param;
	}

	public DbDataParameter[] getParams() {
		return params;
	}

	public String getSQL() {
		return sql;
	}

}
