package edu.hziee.common.http.endpoint;

import java.net.InetSocketAddress;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.http.TransportUtil;
import edu.hziee.common.http.codec.HttpResponseEncoder;
import edu.hziee.common.http.response.DefaultHttpResponseSender;
import edu.hziee.common.http.response.HttpResponseSender;
import edu.hziee.common.lang.Holder;
import edu.hziee.common.lang.IpPortPair;
import edu.hziee.common.lang.KeyTransformer;
import edu.hziee.common.lang.Transformer;
import edu.hziee.common.lang.transport.DefaultHolder;
import edu.hziee.common.lang.transport.Receiver;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: ServerEndpoint.java 66 2012-02-25 01:19:49Z archie $
 */
public class ServerEndpoint implements Endpoint {

  private static final Logger               logger             = LoggerFactory.getLogger(ServerEndpoint.class);

  private Receiver                          messageClosure     = null;
  private Holder                            responseContext    = null;

  private KeyTransformer                    keyTransformer     = new KeyTransformer();

  private Channel                           channel            = null;
  private HttpResponseSender                httpResponseSender = new DefaultHttpResponseSender();
  private Transformer<Object, HttpResponse> responseEncoder    = new HttpResponseEncoder();

  @Override
  public void send(Object bean) {

    if (null != bean) {
      // get request back.
      Object key = keyTransformer.transform(bean);
      if (key == null) {
        return;
      }
      HttpRequest req = (HttpRequest) getResponseContext().get(key);
      if (req == null) {
        return;
      }
      TransportUtil.attachRequest(bean, req);

      doSend(bean);
    }

  }

  @Override
  public void send(Object bean, Receiver receiver) {
    throw new UnsupportedOperationException("not implemented yet!");
  }

  @Override
  public void messageReceived(final Object msg) {

    // save request
    Object key = keyTransformer.transform(msg);
    if (key != null) {
      getResponseContext().put(key, TransportUtil.getRequestOf(msg));
    }

    if (this.messageClosure != null) {
      this.messageClosure.messageReceived(msg);
    }
  }

  @Override
  public void stop() {
    this.responseContext = null;
    this.messageClosure = null;
    this.channel = null;
  }

  @Override
  public void start() {
  }

  private void doSend(Object bean) {
    if (bean != null) {
      HttpResponse response = (HttpResponse) responseEncoder.transform(bean);
      httpResponseSender.sendResponse(channel, response);
    }
  }

  @Override
  public void setChannel(Channel channel) {
    this.channel = channel;
  }

  public void setResponseEncoder(Transformer<Object, HttpResponse> responseEncoder) {
    this.responseEncoder = responseEncoder;
  }

  public void setMessageClosure(Receiver messageClosure) {
    this.messageClosure = messageClosure;
  }

  public void setResponseContext(Holder responseContext) {
    this.responseContext = responseContext;
  }

  public Holder getResponseContext() {
    if (this.responseContext == null) {
      responseContext = new DefaultHolder();
    }
    return responseContext;
  }

  public void setKeyTransformer(KeyTransformer keyTransformer) {
    this.keyTransformer = keyTransformer;
  }

  @Override
  public IpPortPair getRemoteAddress() {
    InetSocketAddress addr = (InetSocketAddress) channel.getRemoteAddress();
    return new IpPortPair(addr.getHostName(), addr.getPort());
  }

}
