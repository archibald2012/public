/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    ExceptionListener.java
 * Creator:     wangqi
 * Create-Date: 2011-5-3 下午05:58:38
 *******************************************************************************/
package edu.hziee.common.queue;

import java.util.List;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: ExceptionListener.java 14 2012-01-10 11:54:14Z archie $
 */
public interface ExceptionListener<T> {
	void onException(List<T> records);
}
