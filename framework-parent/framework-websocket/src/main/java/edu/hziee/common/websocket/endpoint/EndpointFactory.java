package edu.hziee.common.websocket.endpoint;

import org.jboss.netty.channel.Channel;

import edu.hziee.common.lang.Holder;
import edu.hziee.common.lang.transport.Receiver;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: EndpointFactory.java 53 2012-02-19 04:54:55Z archie $
 */
public interface EndpointFactory {

  Endpoint createEndpoint(Channel channel);
  void setMessageClosure(Receiver messageClosure);
  void setResponseContext(Holder responseContext);
}
