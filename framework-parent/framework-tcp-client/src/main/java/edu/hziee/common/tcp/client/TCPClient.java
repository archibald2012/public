package edu.hziee.common.tcp.client;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.service.IoServiceStatistics;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.transport.Receiver;
import edu.hziee.common.serialization.bytebean.codec.bean.BeanFieldCodec;
import edu.hziee.common.serialization.protocol.meta.MsgCode2TypeMetainfo;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: TCPConnector.java 63 2012-02-25 01:12:58Z archie $
 */
public class TCPClient {

	private final Logger				logger					= LoggerFactory.getLogger(getClass());

	private String							name						= "TCPConnector";
	private String							destIp					= null;
	private int									destPort				= -1;
	private long								connectTimeout	= 10000;
	private long								readTimeout			= 60;

	private NioSocketConnector	connector				= null;
	private IoSession						session					= null;

	private MinaXipEncoder			encoder					= new MinaXipEncoder();
	private MinaXipDecoder			decoder					= new MinaXipDecoder();

	private Receiver						receiver				= null;

	public TCPClient(String name) {
		this.name = name;
		this.connector = new NioSocketConnector();
	}

	public void start() {

		this.connector.setConnectTimeoutMillis(connectTimeout);

		this.connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ProtocolCodecFactory() {

			@Override
			public ProtocolEncoder getEncoder(IoSession session) throws Exception {
				return encoder;
			}

			@Override
			public ProtocolDecoder getDecoder(IoSession session) throws Exception {
				return decoder;
			}
		}));
		this.connector.getFilterChain().addLast("executor", new ExecutorFilter(Executors.newFixedThreadPool(2)));

		this.connector.getSessionConfig().setKeepAlive(true);
		this.connector.getSessionConfig().setUseReadOperation(true);

		// session空闲60秒给服务器发空闲的信息，即心跳信息
		this.connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);

		this.connector.setHandler(new IOHandler());

		doConnect();
	}

	public void stop() {
		this.connector.dispose();
	}

	private class IOHandler extends IoHandlerAdapter {
		private final Logger	logger	= LoggerFactory.getLogger(IOHandler.class);

		@Override
		public void messageReceived(IoSession session, Object msg) throws Exception {
			if (logger.isTraceEnabled()) {
				logger.trace("messageReceived: " + msg);
			}

			if (null != receiver) {
				receiver.messageReceived(msg);
			} else {
				logger.warn("No receiver, ignore incoming msg:" + msg);
			}
		}

		@Override
		public void sessionClosed(final IoSession session) throws Exception {
			if (logger.isInfoEnabled()) {
				logger.info("closed session: " + session.getId());
			}
			onSessionClosed(session);
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable e) throws Exception {
			logger.error("transport:", e);
			session.close();
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

		connectFuture.awaitUninterruptibly();

		if (!connectFuture.isConnected()) {
			if (logger.isInfoEnabled()) {
				logger.info(getName() + " connect [" + this.destIp + ":" + this.destPort + "] failed, retry...");
			}
			doConnect();
		} else {
			IoSession session = connectFuture.getSession();
			if (logger.isInfoEnabled()) {
				logger.info("open session: " + session);
			}
			this.session = session;
		}

	}

	public Object send(Object bean) {
		if (session == null) {
			if (logger.isInfoEnabled()) {
				logger.info("send: no sesssion, msg [{}] lost. ", bean);
			}
			return null;
		}

		session.write(bean).awaitUninterruptibly();

		ReadFuture readFuture = session.read();
		if (readFuture.awaitUninterruptibly(readTimeout, TimeUnit.SECONDS)) {
			return readFuture.getMessage();
		} else {
			return null;
		}
	}

	public void setConnectTimeout(long connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setReadTimeout(long readTimeout) {
		this.readTimeout = readTimeout;
	}

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	public String getName() {
		return this.name;
	}

	public void setDestIp(String destIp) {
		this.destIp = destIp;
	}

	public void setDestPort(int destPort) {
		this.destPort = destPort;
	}

	public boolean isConnected() {
		return connector.isActive();
	}

	public IoServiceStatistics getStatistics() {
		return connector.getStatistics();
	}

	public void setDebugEnabled(boolean isDebugEnabled) {
		encoder.setDebugEnabled(isDebugEnabled);
		decoder.setDebugEnabled(isDebugEnabled);
	}

	public void setEncryptKey(String encryptKey) {
		encoder.setEncryptKey(encryptKey);
		decoder.setEncryptKey(encryptKey);
	}

	public void setDumpBytes(int dumpBytes) {
		encoder.setDumpBytes(dumpBytes);
		decoder.setDumpBytes(dumpBytes);
	}

	public void setByteBeanCodec(BeanFieldCodec byteBeanCodec) {
		encoder.setByteBeanCodec(byteBeanCodec);
		decoder.setByteBeanCodec(byteBeanCodec);
	}

	public void setTypeMetaInfo(MsgCode2TypeMetainfo typeMetaInfo) {
		decoder.setTypeMetaInfo(typeMetaInfo);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
