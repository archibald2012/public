package edu.hziee.common.xslt2web.provider;

import edu.hziee.common.xslt2web.data.DbProvider;
import edu.hziee.common.xslt2web.sys.AppSetting;

public final class GlobalProvider {
	private static String sqlType = AppSetting.getCurrent().getSqlProvider();
	private static String dbProviderType = AppSetting.getCurrent()
			.getDbProvider();
	private static DbProvider dbProvider;
	private static SqlProvider sqlProvider;
	private static int commandTimeOut = 600;

	private GlobalProvider() {
	}

	private static DbProvider getDbProvider(String name) {
		DbProviderRegCategory category = (DbProviderRegCategory) AppSetting
				.getCurrent().getRegs().get(DbProviderRegCategory.REG_NAME);
		DbProvider provider = category.newInstance(name);
		//DbProvider.setCurrent(provider);
		return provider;
	}

	private static SqlProvider getSqlProvider(String name) {
		SqlProviderRegCategory category = (SqlProviderRegCategory) AppSetting
				.getCurrent().getRegs().get(SqlProviderRegCategory.REG_NAME);
		SqlProvider provider = category.newInstance(name);
		return provider;
	}

	public static final int getCommandTimeOut() {
		return commandTimeOut;
	}

	public static final void setCommandTimeOut(int commandTimeOut) {
		GlobalProvider.commandTimeOut = commandTimeOut;
	}

	public static final String getSqlType() {
		return sqlType;
	}

	public static final String getDbProviderType() {
		return dbProviderType;
	}

	public static final DbProvider getDbProvider() {
		if (dbProvider == null)
			dbProvider = getDbProvider(dbProviderType);
		return dbProvider;
	}

	public static final SqlProvider getSqlProvider() {
		if (sqlProvider == null)
			sqlProvider = getSqlProvider(sqlType);
		return sqlProvider;
	}

}
