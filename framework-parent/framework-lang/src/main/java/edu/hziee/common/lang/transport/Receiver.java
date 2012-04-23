/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    Receiver.java
 * Creator:     wangqi
 * Create-Date: 2011-4-27 下午08:10:56
 *******************************************************************************/
package edu.hziee.common.lang.transport;


/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: Receiver.java 14 2012-01-10 11:54:14Z archie $
 */
public interface Receiver {
	void messageReceived(Object msg);
}
