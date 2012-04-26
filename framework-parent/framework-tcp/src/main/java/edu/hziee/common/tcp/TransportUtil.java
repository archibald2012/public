
package edu.hziee.common.tcp;

import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;

import edu.hziee.common.lang.Propertyable;
import edu.hziee.common.lang.transport.Sender;
import edu.hziee.common.tcp.endpoint.Endpoint;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: TransportUtil.java 14 2012-01-10 11:54:14Z archie $
 */
public class TransportUtil {

	private static final AttributeKey TRANSPORT_ENDPOINT = new AttributeKey(
			TransportUtil.class, "TRANSPORT_ENDPOINT");

	private static final String TRANSPORT_SENDER = "TRANSPORT_SENDER";

	public static void attachEndpointToSession(IoSession session, Endpoint endpoint) {
		session.setAttribute(TRANSPORT_ENDPOINT, endpoint);
	}

	public static Endpoint getEndpointOfSession(IoSession session) {
		return (Endpoint) session.getAttribute(TRANSPORT_ENDPOINT);
	}

	public static Object attachSender(Object propertyable, Sender sender) {
		if (propertyable instanceof Propertyable) {
			((Propertyable) propertyable).setProperty(TRANSPORT_SENDER, sender);
		}

		return propertyable;
	}

	public static Sender getSenderOf(Object propertyable) {
		if (propertyable instanceof Propertyable) {
			return (Sender) ((Propertyable) propertyable)
					.getProperty(TRANSPORT_SENDER);
		}
		return null;
	}
}
