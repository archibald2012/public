/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    QueryDelegate.java
 * Creator:     Administrator
 * Create-Date: 2011-5-17 下午02:33:45
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
 * @version $Id: QueryDelegate.java 4 2012-01-10 11:51:54Z archie $
 */
public interface QueryDelegate {

	/**
	 * 设置路由
	 * 
	 * @param dbRouteConfig
	 */
	void setDbRouteConfig(DBRouteConfig dbRouteConfig);

	/**
	 * 返回查找对象，将在多库之间分别查询
	 * 
	 * @param statementName
	 * @param parameterObject
	 * @param dr
	 * @return 返回第一个找到的对象
	 */
	Object queryForObject(String statementName, Object parameterObject,
			DBRoute dr);

	/**
	 * 返回查找对象，将在多库之间分别查询
	 * 
	 * @param statementName
	 * @param dr
	 * @return 返回第一个找到的对象
	 */
	Object queryForObject(String statementName, DBRoute dr);

	/**
	 * 返回记录列表，将在多库之间分别查询
	 * 
	 * @param statementName
	 * @param dr
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	List queryForList(String statementName, DBRoute dr);

	/**
	 * 返回记录列表，将在多库之间分别查询
	 * 
	 * @param statementName
	 * @param parameterObject
	 * @param dr
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	List queryForList(String statementName, Object parameterObject, DBRoute dr);

	/**
	 * 返回记录个数，将在多库之间分别查询
	 * 
	 * @param countStatement
	 * @param param
	 * @param dr
	 * @return
	 */
	Integer queryForCount(String countStatement, Object param, DBRoute dr);

	/**
	 * 返回记录个数，将在多库之间分别查询
	 * 
	 * @param countStatement
	 * @param dr
	 * @return
	 */
	Integer queryForCount(String countStatement, DBRoute dr);

	/**
	 * 单库分页查询
	 * 
	 * @param countStatement
	 * @param listStatement
	 * @param param
	 * @param paginator
	 * @param dr
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	List queryForPagedList(String countStatement, String listStatement,
			Object param, Paginator paginator, DBRoute dr);

	/**
	 * 单库分页查询
	 * 
	 * @param countStatement
	 * @param listStatement
	 * @param param
	 * @param paginator
	 * @param dr
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	List queryForPagedList(String listStatement, Object param,
			Paginator paginator, DBRoute dr);

	/**
	 * 多库分页查询
	 * 
	 * @param statementName
	 * @param parameterObject
	 * @param startResult
	 * @param maxResults
	 * @param orderByString
	 *            排序字段，以逗号相隔，“_”前缀表示倒序，示例：filed1,_field2
	 * @param dr
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	List queryForMergedList(String statementName, Object parameterObject,
			Paginator paginator, String orderByString, DBRoute dr);

}
