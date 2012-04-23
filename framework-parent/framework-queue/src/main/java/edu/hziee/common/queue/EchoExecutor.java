/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    EchoExecutor.java
 * Creator:     wangqi
 * Create-Date: 2011-9-14 下午09:53:47
 *******************************************************************************/
package edu.hziee.common.queue;

import java.util.List;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: EchoExecutor.java 14 2012-01-10 11:54:14Z archie $
 */
public class EchoExecutor implements IBatchExecutor<String> {

	@Override
	public void execute(List<String> records) {
		for (String s : records) {
			System.out.println(s);
		}
	}

}
