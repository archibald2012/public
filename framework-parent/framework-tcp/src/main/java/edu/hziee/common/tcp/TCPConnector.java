package edu.hziee.common.tcp;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.service.IoServiceStatistics;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.Holder;
import edu.hziee.common.lang.transport.Receiver;
import edu.hziee.common.lang.transport.Sender;
import edu.hziee.common.lang.transport.SenderSync;
import edu.hziee.common.tcp.endpoint.DefaultEndpointFactory;
import edu.hziee.common.tcp.endpoint.Endpoint;
import edu.hziee.common.tcp.endpoint.EndpointFactory;
import edu.hziee.common.tcp.endpoint.IEndpointChangeListener;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: TCPConnector.java 63 2012-02-25 01:12:58Z archie $
 */
public class TCPConnector implements SenderSync, Sender {

  private static final Logger      logger          = LoggerFactory.getLogger(TCPConnector.class);

  private ScheduledExecutorService exec            = Executors.newSingleThreadScheduledExecutor();

  private String                   name            = "TCPConnector";
  private String                   destIp          = null;
  private int                      destPort        = -1;
  private NioSocketConnector       connector       = null;
  private ProtocolCodecFactory     codecFactory    = null;

  private EndpointFactory          endpointFactory = new DefaultEndpointFactory();

  private long                     retryTimeout    = 1000;

  private Endpoint                 sender;

  public TCPConnector(String name) {
    this.name = name;
    this.connector = new NioSocketConnector();
  }

  public void start() {
    this.connector.setHandler(new IOHandler());
    this.connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(codecFactory));

    doConnect();
  }

  public void stop() {
    this.exec.shutdownNow();
    this.connector.dispose();
    this.sender = null;
  }

  private class IOHandler extends IoHandlerAdapter {
    private final Logger logger = LoggerFactory.getLogger(IOHandler.class);

    @Override
    public void messageReceived(IoSession session, Object msg) throws Exception {
      if (logger.isTraceEnabled()) {
        logger.trace("messageReceived: " + msg);
      }

      Endpoint endpoint = TransportUtil.getEndpointOfSession(session);
      if (null != endpoint) {
        endpoint.messageReceived(TransportUtil.attachSender(msg, endpoint));
      } else {
        logger.warn("missing endpoint, ignore incoming msg:" + msg);
      }
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
      if (logger.isInfoEnabled()) {
        logger.info("open session: " + session);
      }
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
      // create endpoint
      Endpoint endpoint = endpointFactory.createEndpoint(session);
      if (null != endpoint) {
        TransportUtil.attachEndpointToSession(session, endpoint);
        sender = endpoint;
      }
    }

    @Override
    public void sessionClosed(final IoSession session) throws Exception {
      if (logger.isInfoEnabled()) {
        logger.info("closed session: " + session.getId());
      }
      // stop endpoint
      Endpoint endpoint = TransportUtil.getEndpointOfSession(session);
      if (null != endpoint) {
        endpoint.stop();
        sender = null;
      }
      exec.submit(new Runnable() {

        public void run() {
          onSessionClosed(session);
        }
      });
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable e) throws Exception {
      logger.error("transport:", e);
      // 解码有错误的情况下，session不关闄1�7
      // session.close();
    }
  }

  private void onSessionClosed(IoSession session) {
    if (logger.isInfoEnabled()) {
      logger.info(getName() + " session : " + session + "closed, retry connect...");
    }
    doConnect();
  }

  private void doConnect() {
    if (null == destIp || destIp.equals("")) {
      logger.warn(getName() + " destIp is null, disable this connector.");
      return;
    }

    ConnectFuture connectFuture = connector.connect(new InetSocketAddress(destIp, destPort));

    connectFuture.addListener(new IoFutureListener<ConnectFuture>() {

      public void operationComplete(final ConnectFuture connectFuture) {
        exec.submit(new Runnable() {

          public void run() {
            onConnectComplete(connectFuture);
          }
        });
      }
    });
  }

  private void onConnectComplete(ConnectFuture connectFuture) {
    if (!connectFuture.isConnected()) {
      if (logger.isInfoEnabled()) {
        logger.info(getName() + " connect [" + this.destIp + ":" + this.destPort + "] failed, retry...");
      }
      exec.schedule(new Runnable() {

        public void run() {
          doConnect();
        }
      }, retryTimeout, TimeUnit.MILLISECONDS);
    }
  }

  public String getName() {
    return this.name;
  }

  public String getDestIp() {
    return destIp;
  }

  public void setDestIp(String destIp) {
    this.destIp = destIp;
  }

  public int getDestPort() {
    return destPort;
  }

  public void setDestPort(int destPort) {
    this.destPort = destPort;
  }

  public long getRetryTimeout() {
    return retryTimeout;
  }

  public void setRetryTimeout(long retryTimeout) {
    this.retryTimeout = retryTimeout;
  }

  public ProtocolCodecFactory getCodecFactory() {
    return codecFactory;
  }

  public void setCodecFactory(ProtocolCodecFactory codecFactory) {
    this.codecFactory = codecFactory;
  }

  public boolean isConnected() {
    return connector.isActive();
  }

  public IoServiceStatistics getStatistics() {
    return connector.getStatistics();
  }

  public void setReceiver(Receiver receiver) {
    endpointFactory.setReceiver(receiver);
  }

  public void setContext(Holder context) {
    endpointFactory.setContext(context);
  }

  public void setEndpointListener(IEndpointChangeListener endpointListener) {
    endpointFactory.setEndpointListener(endpointListener);
  }

  public void setEndpointFactory(EndpointFactory endpointFactory) {
    this.endpointFactory = endpointFactory;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  @Override
  public void send(Object bean) {
    // 无连接时线程阻塞
    if (sender != null) {
      sender.send(bean);
    } else {
      if (logger.isInfoEnabled()) {
        logger.info("send: no endpoint, msg [{}] lost. ", bean);
      }
    }
  }

  public void send(Object bean, Receiver receiver) {
    if (sender != null) {
      sender.send(bean, receiver);
    } else {
      if (logger.isInfoEnabled()) {
        logger.info("send: no endpoint, msg [{}] lost. ", bean);
      }
    }
  }

  public Object sendAndWait(Object bean) {
    if (sender != null) {
      return sender.sendAndWait(bean);
    } else {
      if (logger.isInfoEnabled()) {
        logger.info("sendAndWait: no endpoint, msg [{}] lost. ", bean);
      }
    }
    return null;
  }

  public Object sendAndWait(Object bean, long timeout, TimeUnit units) {
    if (sender != null) {
      return sender.sendAndWait(bean, timeout, units);
    } else {
      if (logger.isInfoEnabled()) {
        logger.info("sendAndWait: no endpoint, msg [{}] lost. ", bean);
      }
    }
    return null;
  }

}
