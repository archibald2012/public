/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    IBatchInsertor.java
 * Creator:     wangqi
 * Create-Date: 2011-4-30 下午08:11:21
 *******************************************************************************/
package edu.hziee.common.queue;

import java.util.List;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: IBatchExecutor.java 14 2012-01-10 11:54:14Z archie $
 */
public interface IBatchExecutor<T> {

	void execute(List<T> records);
}
