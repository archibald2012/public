/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    EntityDelegate.java
 * Creator:     Administrator
 * Create-Date: 2011-5-17 下午02:33:57
 *******************************************************************************/
package edu.hziee.common.dbroute;

import java.util.List;

import edu.hziee.common.dbroute.config.DBRoute;
import edu.hziee.common.dbroute.config.DBRouteConfig;

/**
 * TODO
 * 
 * @author Administrator
 * @version $Id: EntityDelegate.java 4 2012-01-10 11:51:54Z archie $
 */
public interface EntityDelegate {

	/**
	 * 设置路由
	 * 
	 * @param dbRouteDAOSupport
	 */
	void setDbRouteConfig(DBRouteConfig dbRouteConfig);

	int delete(String statementName, Object parameterObject, DBRoute dr);

	int update(String statementName, Object parameterObject, DBRoute dr);

	Object insert(String statementName, Object parameterObject, DBRoute dr);

	@SuppressWarnings("rawtypes")
	void batchInsert(final String statementName, final List memberList,
			DBRoute dr);

	@SuppressWarnings("rawtypes")
	void batchUpdate(final String statementName, final List memberList,
			DBRoute dr);
}
