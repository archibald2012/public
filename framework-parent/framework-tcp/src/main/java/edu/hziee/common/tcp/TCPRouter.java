/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    TCPRouter.java
 * Creator:     wangqi
 * Create-Date: 2011-5-3 上午09:25:40
 *******************************************************************************/
package edu.hziee.common.tcp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.Closure;
import edu.hziee.common.lang.Holder;
import edu.hziee.common.lang.IpPortPair;
import edu.hziee.common.lang.transport.DefaultHolder;
import edu.hziee.common.lang.transport.Receiver;
import edu.hziee.common.lang.transport.Sender;
import edu.hziee.common.lang.transport.SenderSync;
import edu.hziee.common.tcp.endpoint.IEndpointChangeListener;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: TCPRouter.java 65 2012-02-25 01:16:21Z archie $
 */
public class TCPRouter implements SenderSync, Sender {

  private static final Logger                     logger             = LoggerFactory.getLogger(TCPRouter.class);

  private ConcurrentMap<IpPortPair, TCPConnector> connectors         = new ConcurrentHashMap<IpPortPair, TCPConnector>();

  private ProtocolCodecFactory                    codecFactory       = null;

  private Closure                                 nextClosure        = null;
  private Receiver                                receiver           = null;
  private Holder                                  context            = new DefaultHolder();
  private long                                    retryTimeout       = 1;
  private IEndpointChangeListener                 endpointListener   = null;
  private List<IpPortPair>                        snapshot           = new ArrayList<IpPortPair>();

  private AtomicInteger                           index              = new AtomicInteger(0);

  private String                                  name;

  @Override
  public void send(Object bean) {
    TCPConnector connector = next();
    if (connector != null) {
      if (logger.isTraceEnabled()) {
        logger.trace("send: connector=[{}], bean=[{}]", connector, bean);
      }
      connector.send(bean);
    } else {
      if (logger.isInfoEnabled()) {
        logger.info("send: no route, msg [{}] lost. route=[{}]", bean, name);
      }
    }
  }

  @Override
  public void send(Object bean, Receiver receiver) {
    TCPConnector connector = next();
    if (connector != null) {
      if (logger.isTraceEnabled()) {
        logger.trace("send: connector=[{}], bean=[{}]", connector, bean);
      }
      connector.send(bean, receiver);
    } else {
      if (logger.isInfoEnabled()) {
        logger.info("send: no route, msg [{}] lost. route=[{}]", bean, name);
      }
    }
  }

  @Override
  public Object sendAndWait(Object bean) {
    TCPConnector connector = next();
    if (connector != null) {
      if (logger.isTraceEnabled()) {
        logger.trace("sendAndWait: connector=[{}], bean=[{}]", connector, bean);
      }
      return connector.sendAndWait(bean);
    } else {
      if (logger.isInfoEnabled()) {
        logger.info("send: no route, msg [{}] lost. route=[{}]", bean, name);
      }
      return null;
    }
  }

  @Override
  public Object sendAndWait(Object bean, long timeout, TimeUnit timeUnit) {
    TCPConnector connector = next();
    if (connector != null) {
      if (logger.isTraceEnabled()) {
        logger.trace("sendAndWait: connector=[{}], bean=[{}], timeout=[{}], timeUnit=[{}]", new Object[] { connector, bean, timeout, timeUnit });
      }
      return connector.sendAndWait(bean, timeout, timeUnit);
    } else {
      if (logger.isInfoEnabled()) {
        logger.info("send: no route, msg [{}] lost. route=[{}]", bean, name);
      }
      return null;
    }
  }

  private TCPConnector next() {
    int index = getIndex(snapshot.size());
    if (index >= 0) {
      IpPortPair info = snapshot.get(index);
      return getConnector(info);
    }

    return null;
  }

  private int getIndex(int total) {

    if (total > 0) {
      int next = index.getAndIncrement();
      if (next < 0) {
        next = 0;
        index.set(next);
      }
      return next % total;
    }
    return -1;
  }

  public void doRefreshRoute(List<IpPortPair> infos) {

    Collections.sort(infos);

    if (!snapshot.equals(infos)) {
      if (logger.isInfoEnabled()) {
        logger.info("doRefreshRoute [{}]: update routes info:[{}]/lastRoutes:[{}].", new Object[] { name, infos, snapshot });
      }
      snapshot.clear();
      snapshot.addAll(infos);
    }

    for (IpPortPair info : snapshot) {
      createConnector(info.getIp(), info.getPort());
    }

    // 删除无效连接
    for (IpPortPair key : connectors.keySet()) {
      if (!snapshot.contains(key)) {
        TCPConnector out = connectors.remove(key);
        if (null != out) {
          out.stop();
        }
      }
    }
  }

  private TCPConnector getConnector(IpPortPair info) {
    if (connectors.containsKey(info)) {
      return connectors.get(info);
    } else {
      return null;
    }
  }

  private TCPConnector createConnector(String ip, int port) {
    IpPortPair key = new IpPortPair(ip, port);
    TCPConnector connector = connectors.get(key);

    if (null == connector) {
      connector = new TCPConnector(key.toString());
      TCPConnector oldConnector = connectors.putIfAbsent(key, connector);
      if (null != oldConnector) {
        connector.stop();
        connector = oldConnector;
      } else {
        
        connector.setNextClosure(this.nextClosure);
        connector.setReceiver(this.receiver);
        connector.setContext(this.context);
        connector.setEndpointListener(this.endpointListener);

        connector.setCodecFactory(this.codecFactory);
        connector.setDestIp(ip);
        connector.setDestPort(port);
        connector.setRetryTimeout(this.retryTimeout);

        connector.start();
      }
    }
    return connector;
  }

  public void setHosts(String hosts) {
    try {
      String[] hostArray;
      if (hosts.indexOf("/") == -1) {
        hostArray = new String[] { hosts };
      } else {
        hostArray = hosts.split("/");
      }

      List<IpPortPair> infos = new ArrayList<IpPortPair>();
      for (int i = 0; i < hostArray.length; i++) {
        String ipPort = hostArray[i];
        if (ipPort == null)
          break;
        String[] server = ipPort.split(":");
        if (server.length == 2) {
          IpPortPair ipPortPair = new IpPortPair(server[0].trim(), Integer.parseInt(server[1].trim()));
          infos.add(ipPortPair);
        } else {
          throw new RuntimeException("host [" + ipPort + "] not match IP:PORT");
        }
      }
      this.doRefreshRoute(infos);
    } catch (Exception ex) {
      logger.error(">>>> config occurs error. (hosts ParseException)", ex);
      System.exit(0);
    }
  }

  public void setCodecFactory(ProtocolCodecFactory codecFactory) {
    this.codecFactory = codecFactory;
  }

  public void setNextClosure(Closure nextClosure) {
    this.nextClosure = nextClosure;
  }

  public void setReceiver(Receiver receiver) {
    this.receiver = receiver;
  }

  public void setContext(Holder context) {
    this.context = context;
  }

  public void setEndpointListener(IEndpointChangeListener endpointListener) {
    this.endpointListener = endpointListener;
  }

  public void setRetryTimeout(long retryTimeout) {
    this.retryTimeout = retryTimeout;
  }

  public List<IpPortPair> getSnapshot() {
    return snapshot;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ConcurrentMap<IpPortPair, TCPConnector> getConnectors() {
    return connectors;
  }

}
