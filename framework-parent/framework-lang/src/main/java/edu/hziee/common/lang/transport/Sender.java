/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    Sender.java
 * Creator:     wangqi
 * Create-Date: 2011-4-27 下午08:09:03
 *******************************************************************************/
package edu.hziee.common.lang.transport;


/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: Sender.java 28 2012-01-13 03:02:20Z archie $
 */
public interface Sender {

  void send(Object bean);
  
  void send(Object object, Receiver receiver);

}
