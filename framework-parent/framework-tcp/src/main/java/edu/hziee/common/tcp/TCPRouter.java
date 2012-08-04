package edu.hziee.common.tcp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.Holder;
import edu.hziee.common.lang.IpPortPair;
import edu.hziee.common.lang.RoundRobin;
import edu.hziee.common.lang.transport.DefaultHolder;
import edu.hziee.common.lang.transport.Receiver;
import edu.hziee.common.lang.transport.Sender;
import edu.hziee.common.tcp.endpoint.IEndpointChangeListener;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: TCPRouter.java 65 2012-02-25 01:16:21Z archie $
 */
public class TCPRouter implements Sender {

	private static final Logger											logger						= LoggerFactory.getLogger(TCPRouter.class);

	private ConcurrentMap<IpPortPair, TCPConnector>	connectors				= new ConcurrentHashMap<IpPortPair, TCPConnector>();

	private ProtocolCodecFactory										codecFactory			= null;

	private Receiver																receiver					= null;
	private Holder																	context						= new DefaultHolder();
	private long																		reconnectTimeout	= 1;
	private IEndpointChangeListener									endpointListener	= null;
	private String																	secureId					= null;

	private List<IpPortPair>												snapshot					= new ArrayList<IpPortPair>();
	private RoundRobin<IpPortPair>									rr								= null;

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
				logger.info("send: no route, msg [{}] lost.", bean);
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
				logger.info("send: no route, msg [{}] lost.", bean);
			}
		}
	}

	private TCPConnector next() {
		return rr.hasNext() ? getConnector(rr.next()) : null;
	}

	public void doRefreshRoute(List<IpPortPair> infos) {

		Collections.sort(infos);

		if (!snapshot.equals(infos)) {
			if (logger.isInfoEnabled()) {
				logger.info("doRefreshRoute: update routes info:[{}]/nlastRoutes:[{}].", new Object[] { infos, snapshot });
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

		rr = new RoundRobin<IpPortPair>(snapshot);
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

				connector.setReceiver(this.receiver);
				connector.setContext(this.context);
				connector.setEndpointListener(this.endpointListener);

				connector.setCodecFactory(this.codecFactory);
				connector.setDestIp(ip);
				connector.setDestPort(port);
				connector.setReconnectTimeout(this.reconnectTimeout);
				if (secureId != null) {
					connector.setSecureId(this.secureId);
				}

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

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	public void setContext(Holder context) {
		this.context = context;
	}

	public void setEndpointListener(IEndpointChangeListener endpointListener) {
		this.endpointListener = endpointListener;
	}

	public void setReconnectTimeout(long reconnectTimeout) {
		this.reconnectTimeout = reconnectTimeout;
	}

	public void setSecureId(String secureId) {
		this.secureId = secureId;
	}

	public List<IpPortPair> getSnapshot() {
		return snapshot;
	}

	public ConcurrentMap<IpPortPair, TCPConnector> getConnectors() {
		return connectors;
	}

}
