package edu.hziee.common.tcp.endpoint;

import org.apache.mina.core.session.IoSession;

import edu.hziee.common.lang.Closure;
import edu.hziee.common.lang.Holder;
import edu.hziee.common.lang.transport.DefaultHolder;
import edu.hziee.common.lang.transport.Receiver;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: DefaultEndpointFactory.java 52 2012-02-19 04:51:33Z archie $
 */
public class DefaultEndpointFactory implements EndpointFactory {

  private Closure                 nextClosure      = null;
  private Receiver                receiver         = null;
  private Holder                  context          = new DefaultHolder();
  private IEndpointChangeListener endpointListener = null;

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.taotaosou.common.transport.endpoint.EndpointFactory#createEndpoint(
   * org.apache.mina.core.session.IoSession)
   */
  @Override
  public Endpoint createEndpoint(IoSession session) {
    DefaultEndpoint endpoint = new DefaultEndpoint();

    endpoint.setSession(session);

    endpoint.setNextClosure(this.nextClosure);
    endpoint.setReceiver(this.receiver);
    endpoint.setContext(this.context);
    endpoint.setEndpointListener(this.endpointListener);

    endpoint.start();

    return endpoint;
  }

  public Closure getNextClosure() {
    return nextClosure;
  }

  public void setNextClosure(Closure nextClosure) {
    this.nextClosure = nextClosure;
  }

  public Receiver getReceiver() {
    return receiver;
  }

  public void setReceiver(Receiver receiver) {
    this.receiver = receiver;
  }

  public Holder getContext() {
    return context;
  }

  public void setContext(Holder context) {
    this.context = context;
  }

  @Override
  public void setEndpointListener(IEndpointChangeListener endpointListener) {
    this.endpointListener = endpointListener;
  }

}
