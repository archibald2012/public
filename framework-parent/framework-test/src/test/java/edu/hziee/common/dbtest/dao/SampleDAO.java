/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    SampleDAO.java
 * Creator:     Administrator
 * Create-Date: 2011-5-20 上午10:41:04
 *******************************************************************************/
package edu.hziee.common.dbtest.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import edu.hziee.common.dbtest.domain.Sample;

/**
 * TODO
 * 
 * @author Administrator
 * @version $Id: SampleDAO.java 14 2012-01-10 11:54:14Z archie $
 */
public class SampleDAO extends SqlMapClientDaoSupport {

	public List<Sample> loadAll(long longField) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("longField", longField);
		return getSqlMapClientTemplate()
				.queryForList("LOAD_ALL_SAMPLE", params);
	}
}
