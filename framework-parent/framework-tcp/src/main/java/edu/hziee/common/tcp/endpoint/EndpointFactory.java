
package edu.hziee.common.tcp.endpoint;

import org.apache.mina.core.session.IoSession;

import edu.hziee.common.lang.Closure;
import edu.hziee.common.lang.Holder;
import edu.hziee.common.lang.transport.Receiver;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: EndpointFactory.java 52 2012-02-19 04:51:33Z archie $
 */
public interface EndpointFactory {
  Endpoint createEndpoint(IoSession session);
  void setNextClosure(Closure nextClosure);
  void setReceiver(Receiver receiver);
  void setContext(Holder responseContext);
  void setEndpointListener(IEndpointChangeListener endpointListener);
}
