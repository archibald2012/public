package edu.hziee.common.tcp;

import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.service.IoServiceStatistics;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.Base64;
import edu.hziee.common.lang.Holder;
import edu.hziee.common.lang.RSA;
import edu.hziee.common.lang.transport.Receiver;
import edu.hziee.common.lang.transport.Sender;
import edu.hziee.common.lang.transport.SenderSync;
import edu.hziee.common.tcp.endpoint.DefaultEndpointFactory;
import edu.hziee.common.tcp.endpoint.Endpoint;
import edu.hziee.common.tcp.endpoint.EndpointFactory;
import edu.hziee.common.tcp.endpoint.IEndpointChangeListener;
import edu.hziee.common.tcp.secure.SecureSocketReq;
import edu.hziee.common.tcp.secure.SecureSocketResp;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: TCPConnector.java 63 2012-02-25 01:12:58Z archie $
 */
public class TCPConnector implements SenderSync, Sender {

	private final Logger							logger						= LoggerFactory.getLogger(getClass());

	private String										name							= "TCPConnector";
	private String										destIp						= null;
	private int												destPort					= -1;
	private NioSocketConnector				connector					= null;
	private ProtocolCodecFactory			codecFactory			= null;

	private EndpointFactory						endpointFactory		= new DefaultEndpointFactory();

	private long											reconnectTimeout	= 10;
	private int												idleTime					= -1;
	private boolean										isDebugEnabled		= false;

	private ScheduledExecutorService	connExec					= Executors.newSingleThreadScheduledExecutor();
	private ExecutorService						dispatchExec			= Executors.newFixedThreadPool(2);

	private Endpoint									sender						= null;

	private SecureSocketFilter				secureFilter			= null;

	public TCPConnector(String name) {
		this.name = name;
		this.connector = new NioSocketConnector();
	}

	public void start() {

		if (logger.isDebugEnabled() && isDebugEnabled) {
			this.connector.getFilterChain().addLast("logger", new LoggingFilter());
		}

		if (idleTime > 0) {
			this.connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, idleTime);
		}
		this.connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(codecFactory));
		this.connector.getFilterChain().addLast("executor", new ExecutorFilter(dispatchExec));

		if (secureFilter != null) {
			this.connector.getFilterChain().addLast("secure", secureFilter);
		}

		this.connector.setHandler(new IOHandler());

		doConnect();
	}

	public void stop() {
		this.connExec.shutdownNow();
		this.connector.dispose();
		this.sender = null;
	}

	private class IOHandler extends IoHandlerAdapter {
		private final Logger	logger	= LoggerFactory.getLogger(IOHandler.class);

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
			if (secureFilter == null) {
				Endpoint endpoint = endpointFactory.createEndpoint(session);
				if (null != endpoint) {
					TransportUtil.attachEndpointToSession(session, endpoint);
					sender = endpoint;
				}
			} else {
				SecureSocketReq req = new SecureSocketReq();
				req.setClientPublicKey(Base64.encodeBytesToBytes(secureFilter.getPublicKey().getEncoded()));

				byte[] sourceOfSign = ArrayUtils.addAll(secureFilter.getSecureId().getBytes(), secureFilter.getPublicKey()
						.getEncoded());
				req.setSign(RSA.sign(sourceOfSign, secureFilter.getPrivateKey()));

				session.write(req);
			}
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception {
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
			if (logger.isDebugEnabled()) {
				logger.debug("sessionIdle: " + session + ", status: " + status);
			}
			session.close();
		}

		@Override
		public void sessionClosed(final IoSession session) throws Exception {
			if (logger.isInfoEnabled()) {
				logger.info("closed session: " + session);
			}
			// stop endpoint
			Endpoint endpoint = TransportUtil.getEndpointOfSession(session);
			if (null != endpoint) {
				endpoint.stop();
				sender = null;
			}
			connExec.submit(new Runnable() {

				public void run() {
					onSessionClosed(session);
				}
			});
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable e) throws Exception {
			logger.error("exceptionCaught:", e);
			session.close();
		}
	}

	class SecureSocketFilter extends IoFilterAdapter {

		private final Logger	logger			= LoggerFactory.getLogger(SecureSocketFilter.class);

		private String				secureId		= null;
		private PublicKey			publicKey		= null;
		private PrivateKey		privateKey	= null;

		public SecureSocketFilter(String secureId) {
			this.secureId = secureId;
			KeyPair keys = RSA.genKey();
			this.privateKey = keys.getPrivate();
			this.publicKey = keys.getPublic();
		}

		@Override
		public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {

			if (message instanceof SecureSocketResp) {

				SecureSocketResp resp = (SecureSocketResp) message;
				if (logger.isDebugEnabled()) {
					logger.debug("Reveive SECURE_SOCKET_RESP. session=[{}], resp=[{}]", session, resp);
				}

				byte[] key = RSA.decrypt(resp.getKey(), privateKey);

				if (logger.isDebugEnabled()) {
					logger.debug("key=[{}]", ArrayUtils.toString(key));
				}

				TransportUtil.attachEncryptKeyToSession(session, key);

				Endpoint endpoint = endpointFactory.createEndpoint(session);
				if (null != endpoint) {
					TransportUtil.attachEndpointToSession(session, endpoint);
					sender = endpoint;
				}

			} else {
				nextFilter.messageReceived(session, message);
			}
		}

		public PrivateKey getPrivateKey() {
			return privateKey;
		}

		public PublicKey getPublicKey() {
			return publicKey;
		}

		public String getSecureId() {
			return secureId;
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
				connExec.submit(new Runnable() {

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
			connExec.schedule(new Runnable() {

				public void run() {
					doConnect();
				}
			}, reconnectTimeout, TimeUnit.SECONDS);
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

	public long getReconnectTimeout() {
		return reconnectTimeout;
	}

	public void setSecureId(String secureId) {
		if (secureId != null) {
			this.secureFilter = new SecureSocketFilter(secureId);
		}
	}

	public void setReconnectTimeout(long reconnectTimeout) {
		this.reconnectTimeout = reconnectTimeout;
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

	public boolean isDebugEnabled() {
		return isDebugEnabled;
	}

	public void setDebugEnabled(boolean isDebugEnabled) {
		this.isDebugEnabled = isDebugEnabled;
	}

	public void setThreadSize(int threadSize) {
		this.dispatchExec = Executors.newFixedThreadPool(threadSize);
	}

	public void setIdleTime(int idleTime) {
		this.idleTime = idleTime;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
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
