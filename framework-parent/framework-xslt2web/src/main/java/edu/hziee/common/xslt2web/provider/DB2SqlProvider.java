package edu.hziee.common.xslt2web.provider;

import test.DB2Sequence;
import edu.hziee.common.xslt2web.data.DataSet;
import edu.hziee.common.xslt2web.data.DbCommand;
import edu.hziee.common.xslt2web.data.DbConnection;
import edu.hziee.common.xslt2web.data.IFieldInfo;
import edu.hziee.common.xslt2web.sysutil.StringUtil;

@SqlProviderAnnotation(regName = DB2SqlProvider.REG_NAME, description = "DB2 SQLÓï¾äÉú³ÉÆ÷", author = "YJC", createDate = "2008-09-02")
public class DB2SqlProvider extends SqlProvider {
	static final String REG_NAME = "DB2";

	@Override
	public String getFunction(String funcName, Object... funcParams) {
		if (funcName == null)
			return "";
		funcName = funcName.toUpperCase();
		if ("SUBSTRING".equals(funcName))
			return String.format("SUBSTR(%s, %s, %s)", funcParams);
		else if ("SYSDATE".equals(funcName))
			return "DATE(CURRENT DATE)";

		return "";
	}

	@Override
	public String getListSql(String selectFields, String tableName,
			String whereClause, IFieldInfo[] keyFields, String orderBy,
			int startNum, int number) {
		if (number == 0)
			return String.format("SELECT %s FROM %s %s %s", selectFields,
					tableName, whereClause, orderBy);
		else {
			if (StringUtil.isEmpty(orderBy)) {
				if (keyFields.length == 1)
					orderBy = "ORDER BY " + keyFields[0].getColumnName();
				else {
					orderBy = "ORDER BY ";
					for (int i = 0; i < keyFields.length; ++i) {
						if (i > 0)
							orderBy += ", ";
						orderBy += keyFields[i].getColumnName();
					}
				}
			}
			String internalSelect = "*".equals(selectFields) ? tableName + ".*"
					: selectFields;
			return String
					.format(
							"SELECT %s FROM (SELECT %s, ROWNUMBER() OVER(%s) AS TOOLKIT__ FROM %s %s) AS TK_TABLE WHERE TK_TABLE.TOOLKIT__ BETWEEN %s AND %s",
							selectFields, internalSelect, orderBy, tableName,
							whereClause, startNum, number);
		}
	}

	@Override
	public String getUniID(DbConnection connection, String name) {
		return DB2Sequence.execute(connection, name);
	}

	@Override
	public void setListData(DbCommand command, DataSet dataSet,
			int startRecord, int maxRecords, String srcTable) {
		command.fillDataSet(srcTable, dataSet);
	}

}
