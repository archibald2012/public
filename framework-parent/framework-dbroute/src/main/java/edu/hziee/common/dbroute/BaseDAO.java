/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    BaseDAO.java
 * Creator:     Administrator
 * Create-Date: 2011-5-17 下午02:41:16
 *******************************************************************************/
package edu.hziee.common.dbroute;

import java.util.List;

import edu.hziee.common.dbroute.config.DBRoute;
import edu.hziee.common.dbroute.config.DBRouteConfig;
import edu.hziee.common.lang.Paginator;

/**
 * TODO
 * 
 * @author Administrator
 * @version $Id: BaseDAO.java 4 2012-01-10 11:51:54Z archie $
 */
public class BaseDAO {

	private QueryDelegate queryDelegate = new IbatisQueryDelegate();

	private EntityDelegate entityDelegate = new IbatisEntityDelegate();

	private DBRoute defaultDB;

	public QueryDelegate getQueryDelegate() {
		return queryDelegate;
	}

	public void setQueryDelegate(QueryDelegate queryDelegate) {
		this.queryDelegate = queryDelegate;
	}

	public EntityDelegate getEntityDelegate() {
		return entityDelegate;
	}

	public void setEntityDelegate(EntityDelegate entityDelegate) {
		this.entityDelegate = entityDelegate;
	}

	public DBRoute getDefaultDB() {
		return defaultDB;
	}

	public void setDefaultDB(DBRoute defaultDB) {
		this.defaultDB = defaultDB;
	}

	public void setDbRouteConfig(DBRouteConfig dbRouteConfig) {
		this.queryDelegate.setDbRouteConfig(dbRouteConfig);
		this.entityDelegate.setDbRouteConfig(dbRouteConfig);
	}

	public Object insert(String statementName, Object parameterObject) {
		return getEntityDelegate().insert(statementName, parameterObject,
				getDefaultDB());
	}

	public int update(String statementName, Object parameterObject) {
		return getEntityDelegate().update(statementName, parameterObject,
				getDefaultDB());
	}

	public int delete(String statementName, Object parameterObject) {
		return getEntityDelegate().delete(statementName, parameterObject,
				getDefaultDB());
	}

	@SuppressWarnings("rawtypes")
	public void batchInsert(String statementName, List memberList) {
		getEntityDelegate().batchInsert(statementName, memberList,
				getDefaultDB());
	}

	@SuppressWarnings("rawtypes")
	public void batchUpdate(String statementName, List memberList) {
		getEntityDelegate().batchUpdate(statementName, memberList,
				getDefaultDB());
	}

	public Object queryForObject(String statementName, Object parameterObject) {
		return getQueryDelegate().queryForObject(statementName,
				parameterObject, getDefaultDB());
	}

	public Object queryForObject(String statementName) {
		return getQueryDelegate().queryForObject(statementName, getDefaultDB());
	}

	public List queryForList(String statementName) {
		return getQueryDelegate().queryForList(statementName, getDefaultDB());
	}

	public List queryForList(String statementName, Object parameterObject) {
		return getQueryDelegate().queryForList(statementName, parameterObject,
				getDefaultDB());
	}

	public Integer queryForCount(String countStatement, Object param) {
		return getQueryDelegate().queryForCount(countStatement, param,
				getDefaultDB());
	}

	public Integer queryForCount(String countStatement) {
		return getQueryDelegate().queryForCount(countStatement, getDefaultDB());
	}

	public List queryForPagedList(String countStatement, String listStatement,
			Object parameterObject, Paginator paginator) {
		return getQueryDelegate().queryForPagedList(countStatement,
				listStatement, parameterObject, paginator, getDefaultDB());
	}

	public List queryForMergedList(String statementName,
			Object parameterObject, Paginator paginator, String orderByString) {

		return getQueryDelegate().queryForMergedList(statementName,
				parameterObject, paginator, orderByString, getDefaultDB());
	}

}
