/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    JdbcUtil.java
 * Creator:     Administrator
 * Create-Date: 2011-5-20 上午10:18:37
 *******************************************************************************/
package edu.hziee.common.test.db.util;

import static java.lang.String.format;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.util.Assert;

/**
 * TODO
 * 
 * @author Administrator
 * @version $Id: JdbcUtil.java 14 2012-01-10 11:54:14Z archie $
 */
public class JdbcUtil {
	private static final Logger logger = LoggerFactory
			.getLogger(JdbcUtil.class);

	private static SimpleJdbcTemplate getJdbcTemplate(DataSource dataSource) {
		return new SimpleJdbcTemplate(dataSource);
	}

	/**
	 * 根据id删除表（表必须有id这个字段）中数据
	 * 
	 * @param dataSource
	 *            数据源
	 * @param tableName
	 * @param id
	 */
	public static int deleteDataById(DataSource dataSource, String tableName,
			String idValue) {
		Map<String, String> columnNameValuePairs = new HashMap<String, String>();
		columnNameValuePairs.put("id", idValue);
		return deleteData(dataSource, tableName, columnNameValuePairs);
	}

	/**
	 * 根据主键删除表中数据
	 * 
	 * @param dataSource
	 *            数据源
	 * @param tableName
	 *            表名
	 * @param primaryKeyName
	 *            主键字段名
	 * @param primaryKeyValue
	 */
	public static int deleteDataByPrimaryKey(DataSource dataSource,
			String tableName, String primaryKeyName, String primaryKeyValue) {
		Map<String, String> columnNameValuePairs = new HashMap<String, String>();
		columnNameValuePairs.put(primaryKeyName, primaryKeyValue);
		return deleteData(dataSource, tableName, columnNameValuePairs);
	}

	/**
	 * 根据数据库表字段名称和值删除
	 * 
	 * @param dataSource
	 * @param tableName
	 * @param columnNameValuePairs
	 * @return
	 */
	public static int deleteData(DataSource dataSource, String tableName,
			Map<String, String> columnNameValuePairs) {
		Assert.notNull(dataSource, "dataSource must not be null");
		Assert.notNull(tableName, "tableName must not be null");
		Assert.notEmpty(columnNameValuePairs,
				"columnNameValuePairs must not be empty");
		StringBuffer sql = new StringBuffer("DELETE FROM " + tableName
				+ " WHERE 1=1 ");
		StringBuffer sqlToLog = new StringBuffer(sql);
		Set<String> columnNames = columnNameValuePairs.keySet();
		for (String columnName : columnNames) {
			sql.append(" AND " + columnName + "=:" + columnName);
			sqlToLog.append(" AND " + columnName + "='"
					+ columnNameValuePairs.get(columnName) + "'");
		}
		logger.info(sqlToLog.toString());
		return getJdbcTemplate(dataSource).update(sql.toString(),
				columnNameValuePairs);
	}

	/**
	 * 根据主键查询表（表必须有id这个字段）中数据
	 * 
	 * @param dataSource
	 *            包含数据源信息
	 * @param tableName
	 *            表名
	 * @param idValue
	 *            id值
	 * @return
	 */
	public static List<Map<String, Object>> queryDataById(
			DataSource dataSource, String tableName, String idValue) {
		Map<String, String> columnNameValuePairs = new HashMap<String, String>();
		columnNameValuePairs.put("id", idValue);

		return queryData(dataSource, tableName, columnNameValuePairs);
	}

	/**
	 * 根据主键查询表中数据
	 * 
	 * @param dataSource
	 *            包含数据源信息
	 * @param tableName
	 *            表名
	 * @param primaryKeyName
	 *            主键字段名
	 * @param primaryKeyValue
	 *            主键值
	 * @return
	 */
	public static List<Map<String, Object>> queryDataByPrimaryKey(
			DataSource dataSource, String tableName, String primaryKeyName,
			String primaryKeyValue) {
		Assert.notNull(dataSource, "dataSource must not be null");
		Assert.notNull(tableName, "tableName must not be null");
		Assert.notNull(primaryKeyName, "primaryKeyName must not be null");
		Map<String, String> columnNameValuePairs = new HashMap<String, String>();
		columnNameValuePairs.put(primaryKeyName, primaryKeyValue);

		return queryData(dataSource, tableName, columnNameValuePairs);
	}

	/**
	 * 根据数据库表字段名称和值查询
	 * 
	 * @param dataSource
	 * @param tableName
	 * @param columnNameValuePairs
	 * @return
	 */
	public static List<Map<String, Object>> queryData(DataSource dataSource,
			String tableName, Map<String, String> columnNameValuePairs) {
		Assert.notNull(dataSource, "dataSource must not be null");
		Assert.notNull(tableName, "tableName must not be null");
		Assert.notEmpty(columnNameValuePairs,
				"columnNameValuePairs must not be empty");
		StringBuffer sql = new StringBuffer("SELECT * FROM " + tableName
				+ " WHERE 1=1 ");
		StringBuffer sqlToLog = new StringBuffer(sql);
		Set<String> columnNames = columnNameValuePairs.keySet();
		for (String columnName : columnNames) {
			sql.append(" AND " + columnName + "=:" + columnName);
			sqlToLog.append(" AND " + columnName + "='"
					+ columnNameValuePairs.get(columnName) + "'");
		}
		logger.info(sqlToLog.toString());
		return getJdbcTemplate(dataSource).queryForList(sql.toString(),
				columnNameValuePairs);
	}

	/**
	 * 根据主键判断表（表必须有id这个字段）中是否存在数据
	 * 
	 * @param dataSource
	 * @param tableName
	 * @param idValue
	 * @return
	 */
	public static boolean isExistsInDb(DataSource dataSource, String tableName,
			String idValue) {
		return queryDataById(dataSource, tableName, idValue).size() > 0;
	}

	/**
	 * 根据主键判断表中是否存在数据
	 * 
	 * @param dataSource
	 * @param tableName
	 * @param primaryKeyName
	 * @param primaryKeyValue
	 * @return
	 */
	public static boolean isExistsInDb(DataSource dataSource, String tableName,
			String primaryKeyName, String primaryKeyValue) {
		return queryDataByPrimaryKey(dataSource, tableName, primaryKeyName,
				primaryKeyValue).size() > 0;
	}

	/**
	 * 
	 * @param dataSource
	 * @param tableName
	 * @param columnNameValuePairs
	 * @return
	 */
	public static boolean isExistsInDb(DataSource dataSource, String tableName,
			Map<String, String> columnNameValuePairs) {
		return queryData(dataSource, tableName, columnNameValuePairs).size() > 0;
	}

	/**
	 * 执行DbUnit的DatabaseOperation操作
	 * 
	 * @param dataSource
	 * @param resourceName
	 * @param oper
	 */
	@SuppressWarnings("deprecation")
	public static void dbUnitOperExecute(DataSource dataSource,
			String resourceName, DatabaseOperation oper) {
		Assert.notNull(dataSource, "dataSource must not be null");
		Assert.notNull(resourceName, "resourceName must not be null");

		DatabaseConnection connection;
		Connection dataSourceConnection = null;
		try {
			dataSourceConnection = dataSource.getConnection();
			connection = new DatabaseConnection(dataSourceConnection);

			DatabaseConfig config = connection.getConfig();
			if ("Oracle".equalsIgnoreCase(dataSourceConnection.getMetaData()
					.getDatabaseProductName())) {
				config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
						new OracleDataTypeFactory());
			}

			// build dataSet begin
			ReplacementDataSet dataSet;
			if (resourceName.endsWith(".xls")) {
				dataSet = new ReplacementDataSet(new XlsDataSet(
						new DefaultResourceLoader().getResource(resourceName)
								.getInputStream()));
			} else if (resourceName.endsWith(".xml")) {
				dataSet = new ReplacementDataSet(new FlatXmlDataSet(
						new DefaultResourceLoader().getResource(resourceName)
								.getInputStream()));
			} else {
				String errorMsg = format(
						"Unsupported file type,file '%s' must be xls or xml.",
						resourceName);
				logger.error(errorMsg);
				throw new IllegalStateException(errorMsg);
			}
			dataSet.addReplacementObject("[NULL]", null);
			// build dataSet end
			oper.execute(connection, dataSet);
			logger.info("DbUnit Oper success ");
		} catch (Exception e) {
			logger.error("DbUnitOperExecute failed" + e.getMessage());
			throw new IllegalStateException("DbUnitOperExecute failed"
					+ e.getMessage(), e);
		} finally {
			try {
				dataSourceConnection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 执行DbUnit的INSERT操作
	 * 
	 * @param dataSource
	 * @param resourceName
	 */
	public static void dbUnitInsertData(DataSource dataSource,
			String resourceName) {
		dbUnitOperExecute(dataSource, resourceName, DatabaseOperation.INSERT);
	}

	/**
	 * 执行DbUnit的DELETE操作
	 * 
	 * @param dataSource
	 * @param resourceName
	 */
	public static void dbUnitDeleteData(DataSource dataSource,
			String resourceName) {
		dbUnitOperExecute(dataSource, resourceName, DatabaseOperation.DELETE);
	}

	/**
	 * 执行DbUnit的Refresh操作
	 * 
	 * @param dataSource
	 * @param resourceName
	 */
	public static void dbUnitRefreshData(DataSource dataSource,
			String resourceName) {
		dbUnitOperExecute(dataSource, resourceName, DatabaseOperation.REFRESH);
	}

	/**
	 * dbunit查询数据写入xls
	 * 
	 * @param dataSource
	 * @param tableName
	 * @param query
	 * @param destXlsFile
	 */
	public static void writeDataToExcel(DataSource dataSource,
			String tableName, String query, File destXlsFile) {
		Assert.notNull(dataSource, "dataSource must not be null");
		Assert.notNull(tableName, "tableName must not be empty");
		Assert.notNull(destXlsFile, "destXlsFile must not be null");
		writeDataToExcel(dataSource, new String[] { tableName },
				new String[] { query }, destXlsFile);
	}

	/**
	 * dbunit查询数据写入xls
	 * 
	 * @param dataSource
	 * @param tableNames
	 * @param querys
	 * @param destXlsFile
	 */
	public static void writeDataToExcel(DataSource dataSource,
			String[] tableNames, String[] querys, File destXlsFile) {
		Assert.notNull(dataSource, "dataSource must not be null");
		Assert.notEmpty(tableNames, "tableNames must not be empty");
		Assert.notEmpty(querys, "querys must not be empty");
		Assert.notNull(destXlsFile, "destXlsFile must not be null");
		Assert.state(tableNames.length == querys.length,
				"tableNames'length must be equal to querys'length");
		DatabaseConnection conn;
		try {
			conn = new DatabaseConnection(dataSource.getConnection());
			QueryDataSet queryDataSet = new QueryDataSet(conn);
			for (int i = 0; i < tableNames.length; i++) {
				queryDataSet.addTable(tableNames[i], querys[i]);
			}
			XlsDataSet.write(queryDataSet, new FileOutputStream(destXlsFile));
			logger.info("Dbunit Write Data To Excel: "
					+ destXlsFile.getAbsolutePath());
		} catch (Exception e) {
			logger.error("DbunitWriteDataToExcel failed");
			throw new IllegalStateException("DbunitWriteDataToExcel failed", e);
		}

	}

	/**
	 * 
	 * @param dataSource
	 * @param tableName
	 * @param destXlsFile
	 */
	public static void writeDataToExcel(DataSource dataSource,
			String tableName, File destXlsFile) {
		Assert.notNull(dataSource, "dataSource must not be null");
		Assert.notNull(tableName, "tableName must not be empty");
		Assert.notNull(destXlsFile, "destXlsFile must not be null");
		writeDataToExcel(dataSource, new String[] { tableName }, destXlsFile);
	}

	/**
	 * 
	 * @param dataSource
	 * @param tableNames
	 * @param destXlsFile
	 */
	public static void writeDataToExcel(DataSource dataSource,
			String[] tableNames, File destXlsFile) {
		Assert.notNull(dataSource, "dataSource must not be null");
		Assert.notEmpty(tableNames, "tableNames must not be empty");
		Assert.notNull(destXlsFile, "destXlsFile must not be null");
		DatabaseConnection conn;
		try {
			conn = new DatabaseConnection(dataSource.getConnection());
			QueryDataSet queryDataSet = new QueryDataSet(conn);
			for (int i = 0; i < tableNames.length; i++) {
				queryDataSet.addTable(tableNames[i]);
			}
			XlsDataSet.write(queryDataSet, new FileOutputStream(destXlsFile));
			logger.info("Dbunit Write Data To Excel: "
					+ destXlsFile.getAbsolutePath());
		} catch (Exception e) {
			logger.error("DbunitWriteDataToExcel failed");
			throw new IllegalStateException("DbunitWriteDataToExcel failed", e);
		}
	}

	public void writeFullDataSetToExcel(DataSource dataSource,
			String destXlsFile) {

		DatabaseConnection conn;
		try {
			conn = new DatabaseConnection(dataSource.getConnection());
			XlsDataSet.write(conn.createDataSet(), new FileOutputStream(
					destXlsFile));
			logger.info("Dbunit Write Data To Excel: " + destXlsFile);
		} catch (Exception e) {
			logger.error("DbunitWriteDataToExcel failed");
			throw new IllegalStateException("DbunitWriteDataToExcel failed", e);
		}
	}
}
