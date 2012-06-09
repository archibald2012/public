package edu.hziee.common.xslt2web.easysearch;

import java.util.ArrayList;

import edu.hziee.common.xslt2web.data.DbDataParameter;
import edu.hziee.common.xslt2web.provider.GlobalProvider;
import edu.hziee.common.xslt2web.sys.EventArgs;
import edu.hziee.common.xslt2web.sys.IParamBuilder;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

public class SQLParamEventArgs extends EventArgs {
	private static final long serialVersionUID = 1L;
	
	private int topCount;
	private String select;
	private String tableName;
	private String where;
	private String orderBy;
	private EasySearchSQLType sqlType;
	private boolean searchCache;
	private ArrayList<DbDataParameter> params;

	public SQLParamEventArgs(Object source) {
		super(source);
		params = new ArrayList<DbDataParameter>();
	}

	public final int getTopCount() {
		return topCount;
	}

	public final void setTopCount(int topCount) {
		this.topCount = topCount;
	}

	public final String getSelect() {
		return select;
	}

	public final void setSelect(String select) {
		this.select = select;
	}

	public final String getTableName() {
		return tableName;
	}

	public final void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public final String getWhere() {
		return where;
	}

	public final void setWhere(String where) {
		this.where = where;
	}

	public final String getOrderBy() {
		return orderBy;
	}

	public final void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public final EasySearchSQLType getSqlType() {
		return sqlType;
	}

	public final boolean isSearchCache() {
		return searchCache;
	}

	void setProperties(int topCount, String select, String tableName,
			String where, String orderBy, EasySearchSQLType sqlType,
			boolean searchCache) {
		this.topCount = topCount;
		this.select = select;
		this.tableName = tableName;
		this.where = where;
		this.orderBy = orderBy;
		this.sqlType = sqlType;
		this.searchCache = searchCache;
	}

	public final String getSQL() {
		String whereClause = (StringUtil.isEmpty(this.where)) ? "" : "WHERE "
				+ this.where;
		String orderBy = (StringUtil.isEmpty(this.orderBy)) ? "" : "ORDER BY "
				+ this.orderBy;
		return GlobalProvider.getSqlProvider().getListSql(select, tableName,
				whereClause, null, orderBy, 0, topCount);
	}

	public final String GetTotalSql() {
		String sql = String.format("SELECT %s FROM %s", select, tableName);
		return sql;
	}

	public final void addParams(IParamBuilder paramBuilder) {
		addParams(paramBuilder, true);
	}

	public void addParams(IParamBuilder paramBuilder, boolean clearParams) {
		if (clearParams)
			params.clear();
		for (DbDataParameter param : paramBuilder.getParams())
			params.add(param);
	}

	public final void clear() {
		params.clear();
	}

	public final DbDataParameter[] getParams() {
		DbDataParameter[] result = new DbDataParameter[params.size()];
		return params.toArray(result);
	}
}
