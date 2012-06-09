package edu.hziee.common.xslt2web.provider;

import test.UniIDProc;
import edu.hziee.common.xslt2web.data.DataSet;
import edu.hziee.common.xslt2web.data.DbCommand;
import edu.hziee.common.xslt2web.data.DbConnection;
import edu.hziee.common.xslt2web.data.IFieldInfo;
import edu.hziee.common.xslt2web.sys.AppSetting;

@SqlProviderAnnotation(regName = SqlServerSqlProvider.REG_NAME, description = "SQL Server SQLÓï¾äÉú³ÉÆ÷", author = "YJC", createDate = "2008-05-22")
public class SqlServerSqlProvider extends SqlProvider {
	static final String REG_NAME = "SQL Server";

	@Override
	public String getFunction(String funcName, Object... funcParams) {
		if (funcName == null)
			return "";
		funcName = funcName.toUpperCase();
		if ("SUBSTRING".equals(funcName))
			return String.format("SUBSTRING(%s, %s, %s)", funcParams);
		else if ("SYSDATE".equals(funcName))
			return "getdate()";

		return "";
	}

	@Override
	public String getListSql(String selectFields, String tableName,
			String whereClause, IFieldInfo[] keyFields, String orderBy,
			int startNum, int number) {
		String topCount = (number == 0) ? "" : "TOP " + number;
		return String.format("SELECT %s %s FROM %s %s %s", topCount,
				selectFields, tableName, whereClause, orderBy);
	}

	@Override
	public String getUniID(DbConnection connection, String name) {
		return UniIDProc.executeStoredProc(connection, name, AppSetting
				.getCurrent().getIdStep());
	}

	@Override
	public void setListData(DbCommand command, DataSet dataSet,
			int startRecord, int maxRecords, String srcTable) {
		command.fillDataSet(srcTable, dataSet, startRecord, maxRecords);
	}

}
