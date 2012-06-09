package edu.hziee.common.tcp.endpoint;

import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.Holder;
import edu.hziee.common.lang.transport.DefaultHolder;
import edu.hziee.common.lang.transport.MessageListener;
import edu.hziee.common.lang.transport.MessageBatchListener;
import edu.hziee.common.lang.transport.Receiver;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: DefaultEndpointFactory.java 52 2012-02-19 04:51:33Z archie $
 */
public class DefaultEndpointFactory implements EndpointFactory {

	private static final Logger			logger					= LoggerFactory.getLogger(DefaultEndpointFactory.class);
	
	private Receiver								receiver					= null;
	private Holder									context						= new DefaultHolder();
	private IEndpointChangeListener	endpointListener	= null;

	@Override
	public Endpoint createEndpoint(IoSession session) {
		DefaultEndpoint endpoint = new DefaultEndpoint();

		endpoint.setSession(session);
		endpoint.setContext(context);
		endpoint.setEndpointListener(endpointListener);
		if (receiver instanceof MessageBatchListener) {
			List<MessageListener> listenerList = ((MessageBatchListener) receiver).getListenerList();
			int index = Math.abs(endpoint.hashCode()) % listenerList.size();
			MessageListener messageListener = listenerList.get(index);
			if(logger.isDebugEnabled()){
				logger.debug("Attach lister " + messageListener.getName() + " to endpoint " + endpoint);
			}
			endpoint.setReceiver(messageListener);
		} else {
			endpoint.setReceiver(receiver);
		}

		endpoint.start();

		return endpoint;
	}

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	public void setContext(Holder context) {
		this.context = context;
	}

	@Override
	public void setEndpointListener(IEndpointChangeListener endpointListener) {
		this.endpointListener = endpointListener;
	}

}
