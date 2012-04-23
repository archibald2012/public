/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    Propertable.java
 * Creator:     wangqi
 * Create-Date: 2011-4-27 下午08:26:07
 *******************************************************************************/
package edu.hziee.common.lang;

import java.util.Map;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: Propertyable.java 14 2012-01-10 11:54:14Z archie $
 */
public interface Propertyable {
	Object getProperty(String key);
	Map<String, Object> getProperties();
	void setProperty(String key, Object value);
	void setProperties(Map<String, Object> properties);
}
