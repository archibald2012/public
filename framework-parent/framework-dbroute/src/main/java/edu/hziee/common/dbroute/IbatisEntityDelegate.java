package edu.hziee.common.dbroute;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.ibatis.sqlmap.client.SqlMapExecutor;

import edu.hziee.common.dbroute.config.DBRoute;
import edu.hziee.common.dbroute.config.DBRouteConfig;

/**
 * TODO
 * 
 * @author Administrator
 * @version $Id: IbatisEntityDelegate.java 4 2012-01-10 11:51:54Z archie $
 */
public class IbatisEntityDelegate implements EntityDelegate {

	private static final Logger logger = LoggerFactory
			.getLogger(IbatisEntityDelegate.class);

	private DBRouteConfig dbRouteConfig;

	@Override
	public int delete(String statementName, Object parameterObject, DBRoute dr) {
		Map.Entry<String, SqlMapClientTemplate> e = getSqlMapTemplate(dr,
				statementName);

		String dbName = e.getKey();
		SqlMapClientTemplate st = e.getValue();

		long startTime = System.currentTimeMillis();
		int affectSize = st.delete(statementName, parameterObject);
		long endTime = System.currentTimeMillis();

		logRunTime(statementName, dbName, endTime - startTime);

		return affectSize;
	}

	@Override
	public int update(String statementName, Object parameterObject, DBRoute dr) {
		Map.Entry<String, SqlMapClientTemplate> e = getSqlMapTemplate(dr,
				statementName);

		String dbName = e.getKey();
		SqlMapClientTemplate st = e.getValue();
		long startTime = System.currentTimeMillis();
		int affectSize = st.update(statementName, parameterObject);
		long endTime = System.currentTimeMillis();
		logRunTime(statementName, dbName, endTime - startTime);

		return affectSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taotaosou.common.persistence.EntityDelegate#insert(java.lang.String,
	 * java.lang.Object, com.taotaosou.common.persistence.router.DBRoute)
	 */
	@Override
	public Object insert(String statementName, Object parameterObject,
			DBRoute dr) {
		Map.Entry<String, SqlMapClientTemplate> e = getSqlMapTemplate(dr,
				statementName);

		String dbName = e.getKey();
		SqlMapClientTemplate st = e.getValue();

		long startTime = System.currentTimeMillis();
		Object returnObject = st.insert(statementName, parameterObject);
		long endTime = System.currentTimeMillis();
		logRunTime(statementName, dbName, endTime - startTime);

		return returnObject;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public void batchInsert(final String statementName, final List memberList,
			DBRoute dr) {
		Map.Entry<String, SqlMapClientTemplate> e = getSqlMapTemplate(dr,
				statementName);

		String dbName = e.getKey();
		SqlMapClientTemplate st = e.getValue();

		long startTime = System.currentTimeMillis();

		st.execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor)
					throws SQLException {

				executor.startBatch();
				for (Object tObject : memberList) {
					executor.insert(statementName, tObject);
				}

				executor.executeBatch();
				return null;
			}
		});

		long endTime = System.currentTimeMillis();
		logRunTime(statementName, dbName, endTime - startTime);

		return;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public void batchUpdate(final String statementName, final List memberList,
			DBRoute dr) {
		Map.Entry<String, SqlMapClientTemplate> e = getSqlMapTemplate(dr,
				statementName);

		String dbName = e.getKey();
		SqlMapClientTemplate st = e.getValue();

		long startTime = System.currentTimeMillis();

		st.execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor)
					throws SQLException {

				executor.startBatch();
				for (Object tObject : memberList) {
					executor.update(statementName, tObject);
				}

				executor.executeBatch();
				return null;
			}
		});

		long endTime = System.currentTimeMillis();
		logRunTime(statementName, dbName, endTime - startTime);

		return;
	}

	protected Map.Entry<String, SqlMapClientTemplate> getSqlMapTemplate(
			DBRoute dr, String statementName) {
		Map<String, SqlMapClientTemplate> dbMap = dbRouteConfig
				.getSqlMapTemplates(dr, statementName);

		if (dbMap.isEmpty()) {
			throw new RuntimeException(
					"no database found, please confirm the parameters. DBRoute=["
							+ dr + "], statement=[" + statementName + "]");
		}

		if (dbMap.size() != 1) {
			throw new RuntimeException(
					"more than 1 database found, please confirm the parameters. DBRoute=["
							+ dr + "], statement=[" + statementName + "]");
		}

		return dbMap.entrySet().iterator().next();
	}

	public DBRouteConfig getDbRouteConfig() {
		return dbRouteConfig;
	}

	public void setDbRouteConfig(DBRouteConfig dbRouteConfig) {
		this.dbRouteConfig = dbRouteConfig;
	}

	private void logRunTime(String statementName, String dbName, long runTime) {
		if (logger.isDebugEnabled()) {
			logger.debug("Sql " + statementName + " executed on " + dbName
					+ " databases. Run time estimated: " + runTime + "ms");
		}
	}

}
