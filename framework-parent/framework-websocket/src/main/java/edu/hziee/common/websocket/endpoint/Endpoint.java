package edu.hziee.common.websocket.endpoint;

import org.jboss.netty.channel.Channel;

import edu.hziee.common.lang.IpPortPair;
import edu.hziee.common.lang.transport.Receiver;
import edu.hziee.common.lang.transport.Sender;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: Endpoint.java 66 2012-02-25 01:19:49Z archie $
 */
public interface Endpoint extends Sender, Receiver {

	void stop();

	void start();

	void setChannel(Channel channel);

	IpPortPair getRemoteAddress();
	
	void setEndpointListener(IEndpointChangeListener endpointListener);
	
	void closeSession();
}
