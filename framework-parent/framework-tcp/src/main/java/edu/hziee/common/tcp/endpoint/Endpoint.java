/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    EndPoint.java
 * Creator:     wangqi
 * Create-Date: 2011-4-27 下午08:12:06
 *******************************************************************************/
package edu.hziee.common.tcp.endpoint;

import org.apache.mina.core.session.IoSession;

import edu.hziee.common.lang.Closure;
import edu.hziee.common.lang.Holder;
import edu.hziee.common.lang.IpPortPair;
import edu.hziee.common.lang.transport.Receiver;
import edu.hziee.common.lang.transport.Sender;
import edu.hziee.common.lang.transport.SenderSync;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: Endpoint.java 65 2012-02-25 01:16:21Z archie $
 */
public interface Endpoint extends Sender, SenderSync, Receiver {

  void stop();
  void start();

  void setSession(IoSession session);

  void setNextClosure(Closure nextClosure);
  void setReceiver(Receiver receiver);
  void setContext(Holder context);

  void setEndpointListener(IEndpointChangeListener endpointListener);
  
  IpPortPair getRemoteAddress();
}
