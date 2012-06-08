/**
 * 
 */
package edu.hziee.common.lang.transport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 * 
 */
public class ReceiveListener extends Thread implements Receiver {

	private static final Logger		logger				= LoggerFactory.getLogger(ReceiveListener.class);

	private BlockingQueue<Object>	queue;
	private int										poolSize			= 1024;
	private AtomicBoolean					isTerminating	= new AtomicBoolean(false);
	private AtomicBoolean					isListen			= new AtomicBoolean(true);
	private int										awaitingTime	= 1000;
	private Receiver							receiver;

	public ReceiveListener() {
		queue = new LinkedBlockingQueue<Object>(poolSize);
	}

	@Override
	public void run() {
		while (true) {
			try {

				if (isTerminating.get()) {
					logger.info(getName() + " is terminating");
					// break loop to end the thread.
					break;
				}

				if (!isListen.get()) {
					// suspend the listening
					sleep(1000);
					continue;
				}

				Object msg = queue.poll(awaitingTime, TimeUnit.MILLISECONDS);
				if (msg == null) {
					continue;
				}
				dispatch(msg);
			} catch (Exception e) {
				logger.error("Failed at message handling with error " + e.getMessage(), e);
			}
		}
	}

	public void flush() {
		for (Object msg : getWholeRecords()) {
			dispatch(msg);
		}
	}

	@Override
	public void messageReceived(Object msg) {
		add(msg);
	}

	public void add(Object msg) {
		if (isTerminating.get()) {
			if (logger.isDebugEnabled()) {
				logger.debug(getName() + " is terminating. drop msg=[{}]", msg);
			}
			return;
		}

		if (!full()) {
			boolean ret = queue.add(msg);
			if (logger.isTraceEnabled()) {
				logger.trace("add msg to queue. poolSize=[{}], remainCapacity=[{}], msg=[{}], ret=[{}]", new Object[] {
						poolSize, remainCapacity(), msg, ret });
			}
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("queue [{}] is full.", super.getName());
			}
		}
	}

	public void listen(boolean status) {
		isListen.set(status);
		if (logger.isDebugEnabled()) {
			logger.debug("Set listener " + getName() + " status to " + isListen.get());
		}
	}

	public void terminate() {
		isTerminating.set(true);
		flush();
	}

	private List<Object> getWholeRecords() {
		List<Object> recordsCopy = new ArrayList<Object>();
		synchronized (queue) {
			int num = queue.size();
			if (num != 0) {
				queue.drainTo(recordsCopy, num);
			}
		}
		return recordsCopy;
	}

	private void dispatch(Object msg) {
		if (receiver != null) {
			if (logger.isTraceEnabled()) {
				logger.trace("dispatch message. msg=[{}]", msg);
			}
			receiver.messageReceived(msg);
		} else {
			logger.debug("No receiver defined.");
		}
	}

	public boolean full() {
		return remainCapacity() == 0;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public int remainCapacity() {
		return this.queue.remainingCapacity();
	}

	public int size() {
		return this.queue.size();
	}

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	public void setAwaitingTime(int awaitingTime) {
		this.awaitingTime = awaitingTime;
	}

}
