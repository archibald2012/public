/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    QueryDelegateImpl.java
 * Creator:     Administrator
 * Create-Date: 2011-5-17 下午02:42:16
 *******************************************************************************/
package edu.hziee.common.dbroute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import edu.hziee.common.dbroute.config.DBRoute;
import edu.hziee.common.dbroute.config.DBRouteConfig;
import edu.hziee.common.lang.FieldUtil;
import edu.hziee.common.lang.MapUtil;
import edu.hziee.common.lang.Paginator;

/**
 * TODO
 * 
 * @author Administrator
 * @version $Id: IbatisQueryDelegate.java 4 2012-01-10 11:51:54Z archie $
 */
public class IbatisQueryDelegate implements QueryDelegate {

	private static final Logger logger = LoggerFactory
			.getLogger(IbatisQueryDelegate.class);

	private DBRouteConfig dbRouteConfig;

	/* (non-Javadoc)
	 * @see com.taotaosou.common.persistence.QueryDelegate#queryForCount(java.lang.String, java.lang.Object, com.taotaosou.common.persistence.router.DBRoute)
	 */
	@Override
	public Integer queryForCount(String countStatement, Object param, DBRoute dr) {

		Map<String, SqlMapClientTemplate> dbMap = dbRouteConfig
				.getSqlMapTemplates(dr, countStatement);

		int totalCount = 0;

		for (Map.Entry<String, SqlMapClientTemplate> e : dbMap.entrySet()) {

			String dbName = e.getKey();

			SqlMapClientTemplate st = dbMap.get(dbName);

			long startTime = System.currentTimeMillis();

			Object returnObject = st.queryForObject(countStatement, param);

			long endTime = System.currentTimeMillis();

			logRunTime(countStatement, dbName, endTime - startTime);

			totalCount += ((Integer) returnObject).intValue();
		}

		return totalCount;
	}

	/* (non-Javadoc)
	 * @see com.taotaosou.common.persistence.QueryDelegate#queryForCount(java.lang.String, com.taotaosou.common.persistence.router.DBRoute)
	 */
	@Override
	public Integer queryForCount(String countStatement, DBRoute dr) {
		return queryForCount(countStatement, null, dr);
	}

	/* (non-Javadoc)
	 * @see com.taotaosou.common.persistence.QueryDelegate#queryForList(java.lang.String, com.taotaosou.common.persistence.router.DBRoute)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List queryForList(String statementName, DBRoute dr) {
		return queryForList(statementName, null, dr);
	}

	/* (non-Javadoc)
	 * @see com.taotaosou.common.persistence.QueryDelegate#queryForList(java.lang.String, java.lang.Object, com.taotaosou.common.persistence.router.DBRoute)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List queryForList(String statementName, Object parameterObject,
			DBRoute dr) {

		Map<String, SqlMapClientTemplate> dbMap = dbRouteConfig
				.getSqlMapTemplates(dr, statementName);

		List<Object> resultList = new ArrayList<Object>();

		for (Map.Entry<String, SqlMapClientTemplate> e : dbMap.entrySet()) {

			String dbName = e.getKey();

			SqlMapClientTemplate st = dbMap.get(dbName);

			long startTime = System.currentTimeMillis();

			List list = null;
			if (parameterObject != null) {
				list = st.queryForList(statementName, parameterObject);
			} else {
				list = st.queryForList(statementName);
			}

			long endTime = System.currentTimeMillis();

			logRunTime(statementName, dbName, endTime - startTime);

			setDBRoute(dbName, list);

			resultList.addAll(list);
		}

		return resultList;
	}

	/* (non-Javadoc)
	 * @see com.taotaosou.common.persistence.QueryDelegate#queryForObject(java.lang.String, java.lang.Object, com.taotaosou.common.persistence.router.DBRoute)
	 */
	@Override
	public Object queryForObject(String statementName, Object parameterObject,
			DBRoute dr) {

		Map<String, SqlMapClientTemplate> dbMap = dbRouteConfig
				.getSqlMapTemplates(dr, statementName);

		for (Map.Entry<String, SqlMapClientTemplate> e : dbMap.entrySet()) {

			String dbName = e.getKey();

			SqlMapClientTemplate st = dbMap.get(dbName);

			long startTime = System.currentTimeMillis();

			Object returnObject;
			if (parameterObject != null) {
				returnObject = st
						.queryForObject(statementName, parameterObject);
			} else {
				returnObject = st.queryForObject(statementName);
			}

			long endTime = System.currentTimeMillis();

			logRunTime(statementName, dbName, endTime - startTime);

			if (returnObject != null) {
				setDBRoute(dbName, returnObject);
				return returnObject;
			}
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see com.taotaosou.common.persistence.QueryDelegate#queryForObject(java.lang.String, com.taotaosou.common.persistence.router.DBRoute)
	 */
	@Override
	public Object queryForObject(String statementName, DBRoute dr) {
		return queryForObject(statementName, null, dr);
	}

	/* (non-Javadoc)
	 * @see com.taotaosou.common.persistence.QueryDelegate#queryForPagedList(java.lang.String, java.lang.String, java.lang.Object, com.taotaosou.common.lang.Paginator, com.taotaosou.common.persistence.router.DBRoute)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List queryForPagedList(String countStatement, String listStatement,
			Object param, Paginator paginator, DBRoute dr) {
		Map<String, SqlMapClientTemplate> dbMap = dbRouteConfig
				.getSqlMapTemplates(dr, countStatement);

		if (dbMap.size() != 1) {
			throw new RuntimeException(
					"more than 1 database found, please confirm the parameters. DBRoute=["
							+ dr + "], countStatement=[" + countStatement
							+ "], listStatement=[" + listStatement + "]");
		}

		Integer totalItem = queryForCount(countStatement, param, dr);
		int total = totalItem.intValue();

		paginator.setItems(total);

		List resultList = new ArrayList();

		if (total > 0) {
			resultList = queryForPagedList(listStatement, param, paginator, dr);
		}

		return resultList;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List queryForMergedList(String statementName,
			Object parameterObject, Paginator paginator, String orderByString,
			DBRoute dr) {

		List list = queryForList(statementName, parameterObject, dr);

		final List<SortField> orderByFields = transform2OrderFields(orderByString);

		if (!orderByFields.isEmpty()) {

			//进行排序
			long startTime = System.currentTimeMillis();

			Collections.sort(list, new Comparator<Object>() {

				@Override
				public int compare(Object o1, Object o2) {
					if ((orderByFields == null) || (o1 == null) || (o2 == null)) {
						return 0;
					}

					int result = 0;

					for (SortField field : orderByFields) {
						try {
							String fieldName = field.getFieldName();
							Object v1 = FieldUtil.getFieldValue(o1, fieldName);
							Object v2 = FieldUtil.getFieldValue(o2, fieldName);

							if ((v1 == null) || (v2 == null)) {
								return 0;
							}

							if (v1 instanceof java.lang.Integer) {
								result = ((Integer) v1).compareTo((Integer) v2);
							} else if (v1 instanceof java.util.Date) {
								result = ((Date) v1).compareTo((Date) v2);
							} else if (v1 instanceof java.lang.Double) {
								result = ((Double) v1).compareTo((Double) v2);
							} else if (v1 instanceof java.lang.String) {
								result = ((String) v1).compareTo((String) v2);
							}

							//第一个字段相等时比较第二个字段
							if (result != 0) {
								result *= field.isAsc() ? -1 : 1;
								break;
							}
						} catch (Exception e) {
							logger.warn("", e);
							continue;
						}
					}

					return result;
				}
			});

			long endTime = System.currentTimeMillis();
			if (logger.isDebugEnabled()) {
				logger.debug(statementName + " Merge Sort Run time estimated: "
						+ (endTime - startTime) + "ms");
			}
		}

		return list.subList(paginator.getBeginIndex(), paginator.getEndIndex());
	}

	private List<SortField> transform2OrderFields(String orderByString) {
		List<SortField> orderByList = new ArrayList<SortField>();
		if (orderByString != null) {
			StringTokenizer t = new StringTokenizer(orderByString, ",");
			while (t.hasMoreTokens()) {
				String s = t.nextToken();
				if ((s != null)) {
					s = s.trim();
					if (s.length() > 1) {//字段名称不能只是1个字符
						SortField field = new SortField();
						if (s.charAt(0) == '_') {
							field.setFieldName(s.substring(1));
							field.setAsc(false);
						} else {
							field.setFieldName(s);
							field.setAsc(true);
						}
					}
				}
			}
		}
		return orderByList;
	}

	/**
	 * 查询记录集
	 * 
	 * @param listStatement
	 *            查询SQL ID
	 * @param param
	 *            查询参数
	 * @param paginator
	 *            分页器
	 * @param dr
	 *            数据库路由
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List queryForPagedList(String listStatement, Object param,
			Paginator paginator, DBRoute dr) {
		Map paramsMap = MapUtil.objectToMap(param);

		paramsMap.put("_paging_", "y");
		paramsMap.put("_paging_index_ge_", paginator.getBeginIndex());
		paramsMap.put("_paging_index_lt_", paginator.getEndIndex());
		paramsMap.put("_paging_size_", paginator.getItemsPerPage());

		List result = queryForList(listStatement, paramsMap, dr);

		return result;
	}

	@Override
	public void setDbRouteConfig(DBRouteConfig dbRouteConfig) {
		this.dbRouteConfig = dbRouteConfig;
	}

	/**
	 * 设置Route信息至Domain对象
	 * 
	 * @param dbName
	 * @param o
	 */
	private void setDBRoute(String dbName, Object o) {
		if ((o != null) && BaseDO.class.isAssignableFrom(o.getClass())) {
			((BaseDO) o).setDbRoute(new DBRoute(dbName));
		}
	}

	private void setDBRoute(String dbName, List<Object> list) {
		if (list != null) {
			for (Object o : list) {
				setDBRoute(dbName, o);
			}
		}
	}

	private void logRunTime(String statementName, String dbName, long runTime) {
		if (logger.isDebugEnabled()) {
			logger.debug("Sql " + statementName + " executed on " + dbName
					+ " databases. Run time estimated: " + runTime + "ms");
		}
	}

	private class SortField {
		private String fieldName;
		private boolean asc;

		public String getFieldName() {
			return fieldName;
		}
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
		public boolean isAsc() {
			return asc;
		}
		public void setAsc(boolean asc) {
			this.asc = asc;
		}

	}
}
