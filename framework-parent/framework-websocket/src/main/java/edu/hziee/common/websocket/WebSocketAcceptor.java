package edu.hziee.common.websocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelUpstreamHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.Holder;
import edu.hziee.common.lang.transport.Receiver;
import edu.hziee.common.lang.transport.TransportUtil;
import edu.hziee.common.serialization.protocol.meta.MsgCode2TypeMetainfo;
import edu.hziee.common.websocket.codec.BinaryWebSocketFrameDecoder;
import edu.hziee.common.websocket.endpoint.DefaultEndpointFactory;
import edu.hziee.common.websocket.endpoint.Endpoint;
import edu.hziee.common.websocket.endpoint.EndpointFactory;
import edu.hziee.common.websocket.response.DefaultHttpResponseSender;
import edu.hziee.common.websocket.response.HttpResponseSender;

/**
 * A HTTP server which serves Web Socket requests.
 * <ul>
 * <li>Hixie-76/HyBi-00 Safari 5+, Chrome 4-13 and Firefox 4 supports this standard.
 * <li>HyBi-10 Chrome 14-15, Firefox 7 and IE 10 Developer Preview supports this standard.
 * </ul>
 * This server illustrates support for the different web socket specification versions and will work with:
 * 
 * <ul>
 * <li>Safari 5+ (draft-ietf-hybi-thewebsocketprotocol-00)
 * <li>Chrome 6-13 (draft-ietf-hybi-thewebsocketprotocol-00)
 * <li>Chrome 14+ (draft-ietf-hybi-thewebsocketprotocol-10)
 * <li>Chrome 16+ (RFC 6455 aka draft-ietf-hybi-thewebsocketprotocol-17)
 * <li>Firefox 7+ (draft-ietf-hybi-thewebsocketprotocol-10)
 * </ul>
 * 
 * @author wangqi
 * @version $Id: WebSocketAcceptor.java 59 2012-02-24 08:40:46Z archie $
 */
public class WebSocketAcceptor {

	private static final Logger					logger						= LoggerFactory.getLogger(WebSocketAcceptor.class);

	private static final int						MAX_RETRY					= 20;
	// 30s
	private static final long						RETRY_TIMEOUT			= 30 * 1000;

	private ServerBootstrap							bootstrap;
	private Channel											channel;
	private String											acceptIp					= "0.0.0.0";
	private int													acceptPort				= 8080;
	// in seconds
	private int													idleTime					= -1;

	private BinaryWebSocketFrameDecoder	binaryDecoder			= new BinaryWebSocketFrameDecoder();

	private EndpointFactory							endpointFactory		= new DefaultEndpointFactory();

	// 100M
	private int													maxContentLength	= 100 * 1024 * 1024;

	public WebSocketAcceptor() {
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));
	}

	public void start() throws IOException {

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				// Create a default pipeline implementation.
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("decoder", new HttpRequestDecoder());
				pipeline.addLast("aggregator", new HttpChunkAggregator(maxContentLength));
				pipeline.addLast("encoder", new HttpResponseEncoder());
				pipeline.addLast("handler", new WebSocketServerHandler());
				if (idleTime > 0) {
					pipeline.addLast("idleHandler",
							new IdleStateHandler(new HashedWheelTimer(), 0, 0, idleTime, TimeUnit.SECONDS));
				}
				return pipeline;
			}
		});

		int retryCount = 0;
		boolean binded = false;
		do {
			try {
				channel = bootstrap.bind(new InetSocketAddress(this.acceptIp, this.acceptPort));
				binded = true;
			} catch (ChannelException e) {
				logger.warn("start failed : " + e + ", and retry...");

				// 对绑定异常再次进行尝试
				retryCount++;
				if (retryCount >= MAX_RETRY) {
					// 超过最大尝试次数
					throw e;
				}
				try {
					Thread.sleep(RETRY_TIMEOUT);
				} catch (InterruptedException e1) {
				}
			}
		} while (!binded);

		logger.info("Web socket server start succeed in " + acceptIp + ":" + acceptPort);
	}

	public void stop() {
		if (null != channel) {
			channel.unbind();
			channel = null;
		}
	}

	private class WebSocketServerHandler extends IdleStateAwareChannelUpstreamHandler {

		private static final String				WEBSOCKET_PATH	= "/websocket";

		private WebSocketServerHandshaker	handshaker;
		private HttpResponseSender				responseSender	= new DefaultHttpResponseSender();

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			Object msg = e.getMessage();
			if (msg instanceof HttpRequest) {
				handleHttpRequest(ctx, (HttpRequest) msg);
			} else if (msg instanceof WebSocketFrame) {
				handleWebSocketFrame(ctx, (WebSocketFrame) msg);
			}
		}

		private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
			// Allow only GET methods.
			if (req.getMethod() != HttpMethod.GET) {
				responseSender.sendResponse(channel, HttpResponseStatus.FORBIDDEN);
				return;
			}

			// Send the demo page and favicon.ico
			if (req.getUri().equals("/")) {
				responseSender.sendResponse(channel, HttpResponseStatus.OK,
						WebSocketServerIndexPage.getContent(getWebSocketLocation(req)), "UTF-8", "text/html; charset=UTF-8");
				return;
			} else if (req.getUri().equals("/favicon.ico")) {
				responseSender.sendResponse(channel, HttpResponseStatus.NOT_FOUND);
				return;
			}

			// Handshake
			WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req),
					null, false);
			handshaker = wsFactory.newHandshaker(req);
			if (handshaker == null) {
				wsFactory.sendUnsupportedWebSocketVersionResponse(ctx.getChannel());
			} else {
				handshaker.handshake(ctx.getChannel(), req).addListener(WebSocketServerHandshaker.HANDSHAKE_LISTENER);
			}
		}

		private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

			// Check for closing frame
			if (frame instanceof CloseWebSocketFrame) {
				handshaker.close(ctx.getChannel(), (CloseWebSocketFrame) frame);
				return;
			} else if (frame instanceof PingWebSocketFrame) {
				ctx.getChannel().write(new PongWebSocketFrame(frame.getBinaryData()));
				return;
			} else if (frame instanceof BinaryWebSocketFrame) {
				Object signal = binaryDecoder.transform((BinaryWebSocketFrame) frame);
				if (null != signal) {
					Endpoint endpoint = (Endpoint) ctx.getChannel().getAttachment();
					if (null != endpoint) {
						endpoint.messageReceived(TransportUtil.attachSender(signal, endpoint));
					} else {
						logger.warn("missing endpoint, ignore incoming msg:", signal);
					}
				} else {
					logger.error("content is null, try send back client empty HttpResponse.");
					responseSender.sendResponse(channel, HttpResponseStatus.BAD_REQUEST);
				}
			} else {
				throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass()
						.getName()));
			}

		}

		@Override
		public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			if (logger.isDebugEnabled()) {
				logger.debug("channelClosed: {}", e.getChannel());
			}
			Endpoint endpoint = (Endpoint) e.getChannel().getAttachment();
			if (null != endpoint) {
				endpoint.stop();
			}
		}

		@Override
		public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			if (logger.isDebugEnabled()) {
				logger.debug("channelOpen: [" + e.getChannel() + "]");
			}
			Endpoint endpoint = endpointFactory.createEndpoint(e.getChannel());
			if (null != endpoint) {
				e.getChannel().setAttachment(endpoint);
			}
		}

		@Override
		public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
			if (logger.isInfoEnabled()) {
				logger.info("channelIdle: " + e.getState().name() + " for "
						+ (System.currentTimeMillis() - e.getLastActivityTimeMillis()) + " milliseconds, close channel["
						+ e.getChannel() + "]");
			}
			e.getChannel().close();
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
			logger.error("exceptionCaught: ", e.getCause());
			e.getChannel().close();
		}

		private String getWebSocketLocation(HttpRequest req) {
			return "ws://" + req.getHeader(HttpHeaders.Names.HOST) + WEBSOCKET_PATH;
		}

	}

	public void setMaxContentLength(int maxContentLength) {
		this.maxContentLength = maxContentLength;
	}

	public void setAcceptIp(String acceptIp) {
		this.acceptIp = acceptIp;
	}

	public void setAcceptPort(int acceptPort) {
		this.acceptPort = acceptPort;
	}

	public void setIdleTime(int idleTime) {
		this.idleTime = idleTime;
	}

	public void setBinaryDecoder(BinaryWebSocketFrameDecoder binaryDecoder) {
		this.binaryDecoder = binaryDecoder;
	}

	public void setTypeMetaInfo(MsgCode2TypeMetainfo typeMetaInfo) {
		this.binaryDecoder.setTypeMetaInfo(typeMetaInfo);
	}

	public void setReceiver(Receiver receiver) {
		endpointFactory.setReceiver(receiver);
	}

	public void setContext(Holder context) {
		endpointFactory.setContext(context);
	}

	public void setEndpointFactory(EndpointFactory endpointFactory) {
		this.endpointFactory = endpointFactory;
	}
}

final class WebSocketServerIndexPage {

	private static final String	NEWLINE	= "\r\n";

	public static String getContent(String webSocketLocation) {
		return "<html><head><title>Web Socket Index Page</title></head>" + NEWLINE + "<body>" + NEWLINE
				+ "<script type=\"text/javascript\">" + NEWLINE + "var socket;" + NEWLINE + "if (!window.WebSocket) {"
				+ NEWLINE + "  window.WebSocket = window.MozWebSocket;" + NEWLINE + "}" + NEWLINE + "if (window.WebSocket) {"
				+ NEWLINE + "  socket = new WebSocket(\"" + webSocketLocation + "\");" + NEWLINE
				+ "  socket.onmessage = function(event) {" + NEWLINE + "    var ta = document.getElementById('responseText');"
				+ NEWLINE + "    ta.value = ta.value + '\\n' + event.data" + NEWLINE + "  };" + NEWLINE
				+ "  socket.onopen = function(event) {" + NEWLINE + "    var ta = document.getElementById('responseText');"
				+ NEWLINE + "    ta.value = \"Web Socket opened!\";" + NEWLINE + "  };" + NEWLINE
				+ "  socket.onclose = function(event) {" + NEWLINE + "    var ta = document.getElementById('responseText');"
				+ NEWLINE + "    ta.value = ta.value + \"Web Socket closed\"; " + NEWLINE + "  };" + NEWLINE + "} else {"
				+ NEWLINE + "  alert(\"Your browser does not support Web Socket.\");" + NEWLINE + "}" + NEWLINE + NEWLINE
				+ "function send(message) {" + NEWLINE + "  if (!window.WebSocket) { return; }" + NEWLINE
				+ "  if (socket.readyState == WebSocket.OPEN) {" + NEWLINE + "    socket.send(message);" + NEWLINE
				+ "  } else {" + NEWLINE + "    alert(\"The socket is not open.\");" + NEWLINE + "  }" + NEWLINE + "}"
				+ NEWLINE + "</script>" + NEWLINE + "<form onsubmit=\"return false;\">" + NEWLINE
				+ "<input type=\"text\" name=\"message\" value=\"Hello, World!\"/>"
				+ "<input type=\"button\" value=\"Send Web Socket Data\"" + NEWLINE
				+ "       onclick=\"send(this.form.message.value)\" />" + NEWLINE + "<h3>Output</h3>" + NEWLINE
				+ "<textarea id=\"responseText\" style=\"width:500px;height:300px;\"></textarea>" + NEWLINE + "</form>"
				+ NEWLINE + "</body>" + NEWLINE + "</html>" + NEWLINE;
	}

	private WebSocketServerIndexPage() {
		// Unused
	}
}
