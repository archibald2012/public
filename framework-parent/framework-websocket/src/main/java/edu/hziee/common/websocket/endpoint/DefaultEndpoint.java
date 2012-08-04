package edu.hziee.common.websocket.endpoint;

import java.net.InetSocketAddress;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.Holder;
import edu.hziee.common.lang.IpPortPair;
import edu.hziee.common.lang.KeyTransformer;
import edu.hziee.common.lang.transport.DefaultHolder;
import edu.hziee.common.lang.transport.Receiver;
import edu.hziee.common.websocket.codec.BinaryWebSocketFrameEncoder;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: DefaultEndpoint.java 65 2012-02-25 01:16:21Z archie $
 */
public class DefaultEndpoint implements Endpoint {

	private static final Logger					logger						= LoggerFactory.getLogger(DefaultEndpoint.class);

	private Holder											context						= null;
	private Channel											channel						= null;
	private KeyTransformer							keyTransformer		= new KeyTransformer();
	private Receiver										receiver					= null;

	// TODO add event handler
	private IEndpointChangeListener			endpointListener	= null;

	private BinaryWebSocketFrameEncoder	responseEncoder		= new BinaryWebSocketFrameEncoder();

	@Override
	public void start() {
		if (endpointListener != null) {
			endpointListener.onCreate(this);
		}
	}

	@Override
	public void stop() {
		this.context = null;
		this.receiver = null;
		if (endpointListener != null) {
			endpointListener.onStop(this);
			endpointListener = null;
		}
	}

	@Override
	public void closeChannel() {
		if (this.channel != null) {
			this.channel.close();
		}
	}

	@Override
	public void send(Object bean) {
		if (null != bean) {
			doSend(bean);
		}
	}

	@Override
	public void send(Object bean, Receiver receiver) {
		if (null != bean) {
			Object key = keyTransformer.transform(bean);
			getContext().put(key, receiver);
			doSend(bean);
		}
	}

	@Override
	public void messageReceived(final Object msg) {
		Object key = keyTransformer.transform(msg);
		if (key != null) {
			Object context = getContext().getAndRemove(key);
			if (null != context) {
				try {
					if (context instanceof Receiver) {
						((Receiver) context).messageReceived(msg);
					}
				} catch (Exception e) {
					logger.error("onResponse error.", e);
				}
			} else {
				dispatch(msg);
			}
		}
	}

	private void dispatch(final Object msg) {
		if (logger.isTraceEnabled()) {
			logger.trace("dispatch - " + msg);
		}
		if (this.receiver != null) {
			this.receiver.messageReceived(msg);
		}
	}

	private void doSend(Object msg) {

		BinaryWebSocketFrame binaryWebSocketFrame = responseEncoder.transform(msg);

		ChannelFuture future = channel.write(binaryWebSocketFrame);
		future.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) {
				if (!future.isDone()) {
					logger.error("send mesage failed");
				}
			}
		});
	}

	public boolean isConnected() {
		return channel != null && channel.isConnected();
	}

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	public void setContext(Holder context) {
		this.context = context;
	}

	public Holder getContext() {
		if (this.context == null) {
			context = new DefaultHolder();
		}
		return context;
	}

	public void setEndpointListener(IEndpointChangeListener endpointListener) {
		this.endpointListener = endpointListener;
	}

	@Override
	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public IpPortPair getRemoteAddress() {
		InetSocketAddress addr = (InetSocketAddress) channel.getRemoteAddress();
		return new IpPortPair(addr.getHostName(), addr.getPort());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((channel == null) ? 0 : (int) channel.getId());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultEndpoint other = (DefaultEndpoint) obj;
		if (channel.getId() != other.channel.getId())
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("channel=[").append(channel).append("]");
		return builder.toString();
	}

}
