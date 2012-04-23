/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    DBRouteConfig.java
 * Creator:     Administrator
 * Create-Date: 2011-5-17 下午01:01:39
 *******************************************************************************/
package edu.hziee.common.dbroute.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * TODO
 * 
 * @author Administrator
 * @version $Id: DBRouteConfig.java 4 2012-01-10 11:51:54Z archie $
 */
public class DBRouteConfig {

	//配置所有数据库节点
	private List<String> allNodeNameList = new ArrayList<String>();

	//路由策略默认返回的数据库节点列表,DB XIDs
	private List<String> defaultNodeNameList = new ArrayList<String>();

	//为每条SQL配置执行的DB
	//key is statement id, value is DB XIDs,以逗号分隔
	private Map<String, String> statementRuleMap = new HashMap<String, String>();

	//routing rules.
	private List<DBRouteRule> dbRuleList = new ArrayList<DBRouteRule>();

	private Map<String, SqlMapClient> sqlMapList;
	private Map<String, SqlMapClientTemplate> sqlMapTemplateList = new HashMap<String, SqlMapClientTemplate>();

	public void setNodeRuleMap(Map<String, Properties> nodeRuleMap) {
		if (nodeRuleMap.size() == 0) {
			return;
		}

		for (Iterator<String> it = nodeRuleMap.keySet().iterator(); it
				.hasNext();) {

			String dbName = (String) it.next();

			DBRouteRule dbRule = new DBRouteRule(dbName.trim());

			Properties element = (Properties) nodeRuleMap.get(dbName);
			for (Iterator<Map.Entry<Object, Object>> iter = element.entrySet()
					.iterator(); iter.hasNext();) {
				Map.Entry<Object, Object> entry = iter.next();

				String ruleKey = (String) entry.getKey();
				String routingRule = (String) entry.getValue();

				dbRule.addRule(ruleKey, routingRule);
			}

			dbRuleList.add(dbRule);
		}
	}

	public void setStatementRuleMap(Map<String, String> statementRuleMap) {
		this.statementRuleMap = statementRuleMap;
	}

	public void setDefaultNodeNameList(List<String> defaultNodeNameList) {
		this.defaultNodeNameList = defaultNodeNameList;
	}

	/**
	 * 根据给定的路由策略及查询SQL编号返回对应的数据库节点列表
	 * 
	 * @param dbRoute
	 * @param statement
	 * @return 查找不到时返回默认的节点列表
	 */
	public List<String> routingDB(DBRoute dbRoute, String statement) {
		List<String> nodeNameListByNodeRule = routingDB(dbRoute);

		if ((nodeNameListByNodeRule != null)
				&& !nodeNameListByNodeRule.isEmpty()) {
			return nodeNameListByNodeRule;
		}

		List<String> nodeNameListByStatementRule = routingDB(statement);

		if (nodeNameListByStatementRule != null
				&& !nodeNameListByStatementRule.isEmpty()) {
			return nodeNameListByStatementRule;
		}

		return defaultNodeNameList;
	}

	/**
	 * 根据给定的路由策略返回对应的数据库节点列表
	 * 
	 * @param dbRoute
	 * @return
	 */
	public List<String> routingDB(DBRoute dbRoute) {
		if (null == dbRoute) {
			return new ArrayList<String>();
		}

		List<String> nodeNameList = new ArrayList<String>();

		if (dbRoute.getRoutingStrategy() == DBRoutingStrategy.BY_XID) {
			String xid = dbRoute.getXid();

			if (xid != null) {
				if (xid.indexOf(",") != -1) {
					StringTokenizer st = new StringTokenizer(xid, ",");

					while (st.hasMoreTokens()) {
						String dbxid = st.nextToken();
						dbxid = dbxid.trim();

						if (allNodeNameList.contains(dbxid)) {
							nodeNameList.add(dbxid);
						}
					}

					return nodeNameList;
				} else if (allNodeNameList.contains(xid)) {
					nodeNameList.add(xid);
					return nodeNameList;
				}
			}

		} else if (dbRoute.getRoutingStrategy() == DBRoutingStrategy.BY_ITEM) {
			Map<String, String> items = dbRoute.getItems();
			if (items == null || items.isEmpty()) {
				return nodeNameList;
			}

			for (DBRouteRule routeRule : dbRuleList) {
				if (routeRule.isMatched(items)) {
					nodeNameList.add(routeRule.getDbName());
					return nodeNameList;
				}
			}
		}

		return nodeNameList;
	}
	/**
	 * 根据给定的查询SQL编号返回对应的数据库节点列表
	 * 
	 * @param statement
	 * @return
	 */
	public List<String> routingDB(String statement) {
		if (statement == null) {
			return new ArrayList<String>();
		}

		String xid = (String) statementRuleMap.get(statement);

		if (xid != null) {
			List<String> nodeNameList = new ArrayList<String>();

			if (xid.indexOf(",") != -1) {
				StringTokenizer st = new StringTokenizer(xid, ",");

				while (st.hasMoreTokens()) {
					String dbxid = st.nextToken();
					dbxid = dbxid.trim();

					if (allNodeNameList.contains(dbxid)) {
						nodeNameList.add(dbxid);
					}
				}

				return nodeNameList;
			} else if (allNodeNameList.contains(xid)) {
				nodeNameList.add(xid);
				return nodeNameList;
			}
		}

		return new ArrayList<String>();
	}

	/**
	 * 根据给定的路由策略及查询SQL编号返回对应的数据库列表
	 * 
	 * @param dr
	 * @param sqlId
	 * @return
	 */
	public Map<String, SqlMapClientTemplate> getSqlMapTemplates(DBRoute dr,
			String sqlId) {
		List<String> dbNameList = this.routingDB(dr, sqlId);
		if (null == dbNameList || dbNameList.isEmpty()) {
			throw new RuntimeException(
					"No database found, please confirm the parameters. DBRoute=["
							+ dr + "], statement=[" + sqlId + "]");
		}

		Map<String, SqlMapClientTemplate> retDbList = new HashMap<String, SqlMapClientTemplate>();

		for (int i = 0; i < dbNameList.size(); i++) {
			String dbName = (String) dbNameList.get(i);
			SqlMapClientTemplate o = sqlMapTemplateList.get(dbName);
			if (o != null) {
				retDbList.put(dbName, o);
			}
		}

		return retDbList;
	}

	public Map<String, SqlMapClient> getSqlMapList() {
		return sqlMapList;
	}

	public void setSqlMapList(Map<String, SqlMapClient> sqlMapList) {
		this.sqlMapList = sqlMapList;

		// 创建SqlMapTemplate列表
		for (Iterator<String> it = sqlMapList.keySet().iterator(); it.hasNext();) {
			String dbKey = it.next();
			SqlMapClient sqlMapClient = (SqlMapClient) sqlMapList.get(dbKey);

			SqlMapClientTemplate sqlMT = new SqlMapClientTemplate();
			sqlMT.setSqlMapClient(sqlMapClient);
			sqlMapTemplateList.put(dbKey, sqlMT);
		}

		this.allNodeNameList.addAll(sqlMapList.keySet());
	}

	public Map<String, SqlMapClientTemplate> getSqlMapTemplateList() {
		return sqlMapTemplateList;
	}

	public void setSqlMapTemplateList(
			Map<String, SqlMapClientTemplate> sqlMapTemplateList) {
		this.sqlMapTemplateList = sqlMapTemplateList;
	}

}
