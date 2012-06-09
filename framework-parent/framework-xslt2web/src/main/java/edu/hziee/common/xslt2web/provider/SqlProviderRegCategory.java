package edu.hziee.common.xslt2web.provider;

import edu.hziee.common.xslt2web.sys.RegCategory;

public class SqlProviderRegCategory extends
		RegCategory<SqlProvider, SqlProviderAnnotation> {
	private static final long serialVersionUID = 1L;

	public final static String REG_NAME = "SQL_PROVIDER";
	private final static String DISPLAY_NAME = "数据库SQL生成器";

	public SqlProviderRegCategory() {
		super(REG_NAME, DISPLAY_NAME, "sqlprovider",
				SqlProviderAnnotation.class, SqlProviderAttribute.class);
		
		addRegClass(SqlServerSqlProvider.class);
		addRegClass(OracleSqlProvider.class);
		addRegClass(DB2SqlProvider.class);
	}
}
