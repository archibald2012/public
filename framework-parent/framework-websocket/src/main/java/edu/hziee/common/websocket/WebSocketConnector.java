package edu.hziee.common.websocket;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.Executors;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.Holder;
import edu.hziee.common.lang.transport.Receiver;
import edu.hziee.common.lang.transport.Sender;
import edu.hziee.common.lang.transport.TransportUtil;
import edu.hziee.common.websocket.codec.BinaryWebSocketFrameDecoder;
import edu.hziee.common.websocket.endpoint.DefaultEndpointFactory;
import edu.hziee.common.websocket.endpoint.Endpoint;
import edu.hziee.common.websocket.endpoint.EndpointFactory;
import edu.hziee.common.websocket.endpoint.IEndpointChangeListener;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: WebSocketConnector.java 59 2012-02-24 08:40:46Z archie $
 */
public class WebSocketConnector implements Sender {

	private final Logger								logger						= LoggerFactory.getLogger(WebSocketConnector.class);

	private ClientBootstrap							bootstrap;

	private BinaryWebSocketFrameDecoder	binaryDecoder			= null;
	private EndpointFactory							endpointFactory		= new DefaultEndpointFactory();

	// 100M
	private int													maxContentLength	= 100 * 1024 * 1024;

	private WebSocketClientHandshaker		handshaker				= null;
	private URI													uri								= null;
	private Endpoint										sender						= null;

	public WebSocketConnector(URI uri) {
		this.uri = uri;
		String protocol = uri.getScheme();
		if (!protocol.equals("ws")) {
			throw new IllegalArgumentException("Unsupported protocol: " + protocol);
		}
		this.bootstrap = new ClientBootstrap();
	}

	public void start() {

		bootstrap.setFactory(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors
				.newCachedThreadPool()));

		// Connect with V13 (RFC 6455 aka HyBi-17). You can change it to V08 or V00.
		// If you change it to V00, ping is not supported and remember to change
		// HttpResponseDecoder to WebSocketHttpResponseDecoder in the pipeline.
		handshaker = new WebSocketClientHandshakerFactory().newHandshaker(uri, WebSocketVersion.V13, null, false,
				new HashMap<String, String>());

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = new DefaultChannelPipeline();
				pipeline.addLast("aggregator", new HttpChunkAggregator(maxContentLength));
				pipeline.addLast("decoder", new HttpResponseDecoder());
				pipeline.addLast("encoder", new HttpRequestEncoder());
				pipeline.addLast("ws-handler", new WebSocketClientHandler());
				return pipeline;
			}
		});

		doConnect();
	}

	public void stop() {
		this.sender = null;
		this.bootstrap.releaseExternalResources();
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

	private class WebSocketClientHandler extends SimpleChannelUpstreamHandler {
		private final Logger	logger	= LoggerFactory.getLogger(WebSocketClientHandler.class);

		@Override
		public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			if (logger.isDebugEnabled()) {
				logger.debug("channelConnected: " + e.getChannel());
			}
		}

		@Override
		public void channelDisconnected(ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
			// stop endpoint
			Endpoint endpoint = (Endpoint)ctx.getChannel().getAttachment();
			if (null != endpoint) {
				endpoint.stop();
				sender = null;
			}
			onChannelClosed(e.getChannel());
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
			logger.error("exceptionCaught:", e.getCause());
			e.getChannel().close();
		}

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			if (logger.isTraceEnabled()) {
				logger.trace("messageReceived: " + e.getMessage());
			}

			Channel ch = ctx.getChannel();
			if (!handshaker.isHandshakeComplete()) {
				handshaker.finishHandshake(ch, (HttpResponse) e.getMessage());
				logger.info("Handshake completed, webSocket Client connected!", e.getChannel());
				return;
			}

			if (e.getMessage() instanceof HttpResponse) {
				HttpResponse response = (HttpResponse) e.getMessage();
				throw new Exception("Unexpected HttpResponse (status=" + response.getStatus() + ", content="
						+ response.getContent().toString(CharsetUtil.UTF_8) + ")");
			}

			WebSocketFrame frame = (WebSocketFrame) e.getMessage();
			if (frame instanceof BinaryWebSocketFrame) {
				Object msg = binaryDecoder.transform((BinaryWebSocketFrame) frame);
				if (null != msg) {
					Endpoint endpoint = (Endpoint)e.getChannel().getAttachment();
					if (null != endpoint) {
						endpoint.messageReceived(TransportUtil.attachSender(msg, endpoint));
					} else {
						logger.warn("missing endpoint, ignore incoming msg:" + msg);
					}
				}
			} else if (frame instanceof PongWebSocketFrame) {
				if (logger.isTraceEnabled()) {
					logger.trace("WebSocket Client received pong, [{}]", frame);
				}
			} else if (frame instanceof CloseWebSocketFrame) {
				ch.close();
			} else {
				throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass()
						.getName()));
			}

		}
	}

	private void onChannelClosed(Channel channel) {
		if (logger.isInfoEnabled()) {
			logger.info("Channel : " + channel + " closed, retry connect...");
		}
		doConnect();
	}

	private void doConnect() {
		ChannelFuture connectFuture = bootstrap.connect(new InetSocketAddress(uri.getHost(), uri.getPort()));
		connectFuture.awaitUninterruptibly();

		try {
			handshaker.handshake(connectFuture.getChannel()).syncUninterruptibly();
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Handshake [" + this.uri + "] failed, retry...", e);
			}
			doConnect();
		}

		Endpoint endpoint = endpointFactory.createEndpoint(connectFuture.getChannel());
		if (null != endpoint) {
			connectFuture.getChannel().setAttachment(endpoint);
			sender = endpoint;
		}

	}

	public void setBinaryDecoder(BinaryWebSocketFrameDecoder binaryDecoder) {
		this.binaryDecoder = binaryDecoder;
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

	public void setMaxContentLength(int maxContentLength) {
		this.maxContentLength = maxContentLength;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
