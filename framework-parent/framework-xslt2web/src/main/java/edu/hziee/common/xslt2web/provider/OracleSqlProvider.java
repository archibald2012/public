package edu.hziee.common.xslt2web.provider;

import edu.hziee.common.xslt2web.data.DataSet;
import edu.hziee.common.xslt2web.data.DbCommand;
import edu.hziee.common.xslt2web.data.DbConnection;
import edu.hziee.common.xslt2web.data.IFieldInfo;
import edu.hziee.common.xslt2web.data.Sequence;

@SqlProviderAnnotation(regName = OracleSqlProvider.REG_NAME, description = "Oracle SQLÓï¾äÉú³ÉÆ÷", author = "YJC", createDate = "2008-05-22")
public class OracleSqlProvider extends SqlProvider {
	static final String REG_NAME = "Oracle";

	@Override
	public String getFunction(String funcName, Object... funcParams) {
		if (funcName == null)
			return "";
		funcName = funcName.toUpperCase();
		if ("SUBSTRING".equals(funcName))
			return String.format("SUBSTR(%s, %s, %s)", funcParams);
		else if ("SYSDATE".equals(funcName))
			return "SYSDATE";

		return "";
	}

	@Override
	public String getListSql(String selectFields, String tableName,
			String whereClause, IFieldInfo[] keyFields, String orderBy,
			int startNum, int number) {
		if (number == 0)
			return String.format("SELECT %s FROM %s %s %s", selectFields,
					tableName, whereClause, orderBy);
		else
			return String
					.format(
							"SELECT * FROM (SELECT ROWNUM TOOLKIT__, DD.* FROM (SELECT %s "
									+ " FROM %s %s %s) DD WHERE ROWNUM <= %s) YY WHERE TOOLKIT__ > %s",
							selectFields, tableName, whereClause, orderBy,
							number, startNum);
	}

	@Override
	public String getUniID(DbConnection connection, String name) {
		return Sequence.execute(connection, name);
	}

	@Override
	public void setListData(DbCommand command, DataSet dataSet,
			int startRecord, int maxRecords, String srcTable) {
		command.fillDataSet(srcTable, dataSet);
	}
}
