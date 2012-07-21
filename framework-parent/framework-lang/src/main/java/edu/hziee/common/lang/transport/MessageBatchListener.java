/**
 * 
 */
package edu.hziee.common.lang.transport;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 * 
 */
public class MessageBatchListener implements Receiver {

	private static final Logger		logger				= LoggerFactory.getLogger(MessageBatchListener.class);

	private String								name					= "MessageBatchListener";
	private int										threads				= 1;
	private List<MessageListener>	listenerList	= new ArrayList<MessageListener>();
	private int										awaitingTime	= 1000;
	private int										poolSize			= 1024;
	private Receiver							receiver			= null;

	public void start() {
		if (threads <= 0) {
			String error = "Cannot create batch listener with threads " + threads;
			logger.error(error);
			throw new IllegalArgumentException(error);
		}

		for (int i = listenerList.size(); i < threads; i++) {
			StringBuilder builder = new StringBuilder(name);
			builder.append(".listener.").append(i);
			MessageListener listener = new MessageListener(builder.toString(), poolSize, receiver);
			listener.setAwaitingTime(awaitingTime);
			listenerList.add(listener);
			if (logger.isDebugEnabled()) {
				logger.debug("Starting listener thread " + listener);
			}
			listener.start();
			listener.listen(true);
		}
	}

	public void stop() {
		for (MessageListener listener : listenerList) {
			listener.terminate();
		}
	}

	@Override
	public void messageReceived(Object msg) {
		int index = msg.hashCode() % listenerList.size();
		listenerList.get(index).add(msg);
	}

	public List<MessageListener> getListenerList() {
		return listenerList;
	}

	public void setAwaitingTime(int awaitingTime) {
		this.awaitingTime = awaitingTime;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

}
