/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    EndPoint.java
 * Creator:     wangqi
 * Create-Date: 2011-4-27 下午08:12:06
 *******************************************************************************/
package edu.hziee.common.http.endpoint;

import org.jboss.netty.channel.Channel;

import edu.hziee.common.lang.IpPortPair;
import edu.hziee.common.lang.transport.Receiver;
import edu.hziee.common.lang.transport.Sender;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: Endpoint.java 66 2012-02-25 01:19:49Z archie $
 */
public interface Endpoint extends Sender, Receiver {

  void stop();
  void start();

  void setChannel(Channel channel);

  IpPortPair getRemoteAddress();
}
