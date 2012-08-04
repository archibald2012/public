package edu.hziee.common.websocket.endpoint;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.channel.Channel;

import edu.hziee.common.lang.Holder;
import edu.hziee.common.lang.transport.DefaultHolder;
import edu.hziee.common.lang.transport.Receiver;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: DefaultEndpointFactory.java 53 2012-02-19 04:54:55Z archie $
 */
public class DefaultEndpointFactory implements EndpointFactory {

  private Receiver messageClosure  = null;
  private Holder   responseContext = new DefaultHolder();

  @Override
  public Endpoint createEndpoint(Channel channel) {
    ServerEndpoint endpoint = new ServerEndpoint();

    endpoint.setChannel(channel);
    endpoint.setMessageClosure(this.messageClosure);
    endpoint.setResponseContext(this.responseContext);

    endpoint.start();

    return endpoint;
  }

  @Override
  public void setMessageClosure(Receiver messageClosure) {
    this.messageClosure = messageClosure;
  }

  @Override
  public void setResponseContext(Holder responseContext) {
    this.responseContext = responseContext;
  }

}
