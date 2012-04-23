/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    Holder.java
 * Creator:     wangqi
 * Create-Date: 2011-6-15 上午09:28:09
 *******************************************************************************/
package edu.hziee.common.lang;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: Holder.java 14 2012-01-10 11:54:14Z archie $
 */
public interface Holder {
	public void put(Object key, Object value);

	public Object get(Object key);

	public Object getAndRemove(Object key);

	public void remove(Object key);
}
