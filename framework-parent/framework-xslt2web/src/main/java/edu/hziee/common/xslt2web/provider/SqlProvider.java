package edu.hziee.common.xslt2web.provider;

import edu.hziee.common.xslt2web.data.DataSet;
import edu.hziee.common.xslt2web.data.DbCommand;
import edu.hziee.common.xslt2web.data.DbConnection;
import edu.hziee.common.xslt2web.data.IFieldInfo;

public abstract class SqlProvider {

	protected SqlProvider() {
	}

	public abstract String getUniID(DbConnection connection, String name);

	public abstract String getFunction(String funcName, Object... funcParams);

	public abstract String getListSql(String selectFields, String tableName,
			String whereClause, IFieldInfo[] keyFields, String orderBy,
			int startNum, int number);

	public abstract void setListData(DbCommand command, DataSet dataSet,
			int startRecord, int maxRecords, String srcTable);
}
