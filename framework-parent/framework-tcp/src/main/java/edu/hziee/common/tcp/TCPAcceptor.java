package edu.hziee.common.tcp;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.security.PublicKey;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.DES;
import edu.hziee.common.lang.Holder;
import edu.hziee.common.lang.RSA;
import edu.hziee.common.lang.transport.Receiver;
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
 * @version $Id: TCPAcceptor.java 52 2012-02-19 04:51:33Z archie $
 */
public class TCPAcceptor {
	private final Logger					logger					= LoggerFactory.getLogger(getClass());

	private int										maxRetryCount		= 20;
	private long									retryTimeout		= 30 * 1000;														// 30s
	private int										idleTime				= -1;
	private String								acceptIp				= "0.0.0.0";
	private int										acceptPort			= 7777;
	private NioSocketAcceptor			acceptor				= new NioSocketAcceptor();

	private ProtocolCodecFactory	codecFactory		= null;

	private EndpointFactory				endpointFactory	= new DefaultEndpointFactory();

	private SecureSocketFilter		secureFilter		= null;

	public void start() throws Exception {
		acceptor.setReuseAddress(true);
		acceptor.getSessionConfig().setReadBufferSize(2048);
		if (idleTime > 0) {
			acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, idleTime);
		}

		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(codecFactory));
		if (secureFilter != null) {
			acceptor.getFilterChain().addLast("secure", secureFilter);
		}

		acceptor.setHandler(new IOHandler());

		int retryCount = 0;
		boolean binded = false;
		do {
			try {
				acceptor.bind(new InetSocketAddress(acceptIp, acceptPort));
				binded = true;
			} catch (BindException e) {
				logger.warn("start failed on port:[{}], " + e + ", and retry...", acceptPort);
				// 对绑定异常再次进行尝试
				retryCount++;
				if (retryCount >= maxRetryCount) {
					// 超过最大尝试次数
					throw e;
				}
				try {
					Thread.sleep(retryTimeout);
				} catch (InterruptedException e1) {
				}
			} catch (IOException e) {
				// 对其它IO异常继续抛出
				throw e;
			}
		} while (!binded);

		logger.info("start succeed in " + acceptIp + ":" + acceptPort);

	}

	public void stop() {
		this.acceptor.dispose();
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
				logger.warn("missing endpoint, ignore incoming msg [{}]", msg);
			}
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception {
			if (logger.isInfoEnabled()) {
				logger.info("sessionOpened: " + session);
			}
			if (secureFilter == null) {
				Endpoint endpoint = endpointFactory.createEndpoint(session);
				if (null != endpoint) {
					TransportUtil.attachEndpointToSession(session, endpoint);
				}
			}
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception {

		}

		@Override
		public void sessionClosed(final IoSession session) throws Exception {
			if (logger.isDebugEnabled()) {
				logger.debug("sessionClosed: " + session);
			}
			Endpoint endpoint = TransportUtil.getEndpointOfSession(session);
			if (null != endpoint) {
				endpoint.stop();
			}
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
			if (logger.isDebugEnabled()) {
				logger.debug("sessionIdle: " + session + ", status: " + status);
			}
			session.close();
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable e) throws Exception {
			if (logger.isDebugEnabled()) {
				logger.debug("exceptionCaught: " + e.getMessage());
			}
			session.close();
		}
	}

	class SecureSocketFilter extends IoFilterAdapter {

		private final Logger	logger		= LoggerFactory.getLogger(SecureSocketFilter.class);

		private String				secureId	= null;

		public SecureSocketFilter(String secureId) {
			this.secureId = secureId;
		}

		@Override
		public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {

			if (message instanceof SecureSocketReq) {

				SecureSocketReq req = (SecureSocketReq) message;

				if (logger.isDebugEnabled()) {
					logger.debug("Receive SECURE_SOCKET_REQ. session=[{}], req=[{}] ", session, req);
				}

				byte[] clientKeyBytes = req.getClientPublicKey();

				byte[] sourceOfSign = ArrayUtils.addAll(secureId.getBytes(), clientKeyBytes);

				PublicKey clientPublicKey = RSA.decodePublicKey(clientKeyBytes);
				if (RSA.verify(sourceOfSign, clientPublicKey, req.getSign())) {

					if (logger.isDebugEnabled()) {
						logger.debug("Signature verify succeed. session=[{}], secureId=[{}] ", session, secureId);
					}

					// TODO generate by HD
					byte[] key = DES.genKey();

					SecureSocketResp resp = new SecureSocketResp();
					resp.setIdentification(req.getIdentification());
					resp.setKey(RSA.encrypt(key, clientPublicKey));

					session.write(resp);

					TransportUtil.attachEncryptKeyToSession(session, key);

					Endpoint endpoint = endpointFactory.createEndpoint(session);
					if (null != endpoint) {
						TransportUtil.attachEndpointToSession(session, endpoint);
					}

					if (logger.isDebugEnabled()) {
						logger.debug("Send SECURE_SOCKET_RESP. session=[{}], resp=[{}] ", session, resp);
					}

				} else {
					if (logger.isInfoEnabled()) {
						logger.info("Signature verify failed, close session immediately! session=[{}], expected_secureId=[{}]",
								new Object[] { session, secureId });
					}
					session.close(true);
				}

			} else {
				nextFilter.messageReceived(session, message);
			}
		}
	}

	public int getMaxRetryCount() {
		return maxRetryCount;
	}

	public void setMaxRetryCount(int maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
	}

	public long getRetryTimeout() {
		return retryTimeout;
	}

	public void setRetryTimeout(long retryTimeout) {
		this.retryTimeout = retryTimeout;
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

	public String getAcceptIp() {
		return acceptIp;
	}

	public void setAcceptIp(String acceptIp) {
		this.acceptIp = acceptIp;
	}

	public int getAcceptPort() {
		return acceptPort;
	}

	public void setAcceptPort(int acceptPort) {
		this.acceptPort = acceptPort;
	}

	public ProtocolCodecFactory getCodecFactory() {
		return codecFactory;
	}

	public void setSecureId(String secureId) {
		if (secureId != null) {
			this.secureFilter = new SecureSocketFilter(secureId);
		}
	}

	public void setCodecFactory(ProtocolCodecFactory codecFactory) {
		this.codecFactory = codecFactory;
	}

	public void setIdleTime(int idleTime) {
		this.idleTime = idleTime;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
