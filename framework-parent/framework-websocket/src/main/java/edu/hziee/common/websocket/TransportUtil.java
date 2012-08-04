/**
 * 
 */
package edu.hziee.common.websocket;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelLocal;
import org.jboss.netty.handler.codec.http.HttpRequest;

import edu.hziee.common.lang.Propertyable;
import edu.hziee.common.lang.transport.Sender;
import edu.hziee.common.websocket.endpoint.Endpoint;

/**
 * @author ubuntu-admin
 * 
 */
public class TransportUtil {
	private static final String									TRANSPORT_SENDER	= "TRANSPORT_SENDER";
	private static final String									HTTP_REQUEST			= "httpRequest";

	public static final ChannelLocal<Endpoint>	endpoints					= new ChannelLocal<Endpoint>();

	public static void attachEndpointToSession(Channel channel, Endpoint endpoint) {
		// TODO
		// Netty allow to set/get attachment on Channel in the near feature, e.g.
		// channel.getAttribute(new AttributeKey<Endpoint>(TRANSPORT_SENDER))
		endpoints.set(channel, endpoint);
	}

	public static Endpoint getEndpointOfSession(Channel channel) {
		return (Endpoint) endpoints.get(channel);
	}

	public static Object attachSender(Object propertyable, Sender sender) {
		if (propertyable instanceof Propertyable) {
			((Propertyable) propertyable).setProperty(TRANSPORT_SENDER, sender);
		}

		return propertyable;
	}

	public static Sender getSenderOf(Object propertyable) {
		if (propertyable instanceof Propertyable) {
			return (Sender) ((Propertyable) propertyable).getProperty(TRANSPORT_SENDER);
		}
		return null;
	}

	public static Object attachRequest(Object propertyable, HttpRequest request) {
		if (propertyable instanceof Propertyable) {
			((Propertyable) propertyable).setProperty(HTTP_REQUEST, request);
		}

		return propertyable;
	}

	public static HttpRequest getRequestOf(Object propertyable) {
		if (propertyable instanceof Propertyable) {
			return (HttpRequest) ((Propertyable) propertyable).getProperty(HTTP_REQUEST);
		}
		return null;
	}

}
