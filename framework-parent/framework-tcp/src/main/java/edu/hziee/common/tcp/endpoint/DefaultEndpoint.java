package edu.hziee.common.tcp.endpoint;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.Closure;
import edu.hziee.common.lang.Holder;
import edu.hziee.common.lang.IpPortPair;
import edu.hziee.common.lang.KeyTransformer;
import edu.hziee.common.lang.transport.DefaultHolder;
import edu.hziee.common.lang.transport.Receiver;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: DefaultEndpoint.java 65 2012-02-25 01:16:21Z archie $
 */
public class DefaultEndpoint implements Endpoint {

	private static final Logger			logger					= LoggerFactory.getLogger(DefaultEndpoint.class);

	private Closure									nextClosure			= null;
	private Receiver								receiver				= null;
	private Holder									context					= null;

	private IoSession								session					= null;

	private int											waitTimeout			= 10000;

	private KeyTransformer					keyTransformer	= new KeyTransformer();

	private IEndpointChangeListener	endpointListener;

	class ResponseFuture<V> extends FutureTask<V> {

		public ResponseFuture() {
			super(new Callable<V>() {
				public V call() throws Exception {
					return null;
				}
			});
		}

		public void set(V v) {
			super.set(v);
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
	public Object sendAndWait(Object bean) {
		return sendAndWait(bean, waitTimeout, TimeUnit.MILLISECONDS);
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public Object sendAndWait(Object bean, long duration, TimeUnit units) {
		if (null == bean) {
			return null;
		}

		Object key = keyTransformer.transform(bean);
		ResponseFuture responseFuture = new ResponseFuture();
		getContext().put(key, responseFuture);

		doSend(bean);
		try {
			return responseFuture.get(duration, units);
		} catch (Exception e) {
			logger.error("", e);
			return null;
		} finally {
			responseFuture = (ResponseFuture) getContext().getAndRemove(key);
			if (responseFuture != null) {
				responseFuture.cancel(false);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void messageReceived(final Object msg) {
		Object key = keyTransformer.transform(msg);
		if (key != null) {
			Object context = getContext().getAndRemove(key);
			if (null != context) {
				try {
					if (context instanceof ResponseFuture) {
						((ResponseFuture) context).set(msg);
					}
					if (context instanceof Receiver) {
						((Receiver) context).messageReceived(msg);
					}
				} catch (Exception e) {
					logger.error("onResponse error.", e);
				}
			} else {
				if (this.receiver != null) {
					this.receiver.messageReceived(msg);
				}
			}
		}
		if (null != nextClosure) {
			this.nextClosure.execute(msg);
		}
	}

	@Override
	public void stop() {
		this.context = null;
		this.nextClosure = null;
		this.receiver = null;
		if (endpointListener != null) {
			endpointListener.onStop(this);
			endpointListener = null;
		}
	}

	@Override
	public void start() {
		if (endpointListener != null) {
			endpointListener.onCreate(this);
		}
	}

	private void doSend(Object msg) {
		if (msg != null) {
			WriteFuture future = session.write(msg);
			future.addListener(new IoFutureListener<WriteFuture>() {
				public void operationComplete(WriteFuture future) {
					if (!future.isWritten()) {
						// TODO 失败的消息是否重发
						// addToPending(bean);
						if (null != future.getException()) {
							logger.error("send mesage failed, reason:", future.getException());
						} else {
							logger.error("send mesage failed without reason");
						}
					}
				}
			});
		}
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

	public Holder getContext() {
		if (this.context == null) {
			context = new DefaultHolder();
		}
		return context;
	}

	public void setWaitTimeout(int sendTimeout) {
		this.waitTimeout = sendTimeout;
	}

	public void setEndpointListener(IEndpointChangeListener endpointListener) {
		this.endpointListener = endpointListener;
	}

	@Override
	public void setSession(IoSession session) {
		this.session = session;
	}

	@Override
	public IpPortPair getRemoteAddress() {
		InetSocketAddress addr = (InetSocketAddress) session.getRemoteAddress();
		return new IpPortPair(addr.getHostName(), addr.getPort());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultEndpoint other = (DefaultEndpoint) obj;
		if (session != other.session)
			return false;
		return true;
	}
}
