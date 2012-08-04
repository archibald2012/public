package edu.hziee.common.tcp;

import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;

import edu.hziee.common.tcp.endpoint.Endpoint;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: TransportUtil.java 14 2012-01-10 11:54:14Z archie $
 */
public class IoSessionUtil {

	private static final AttributeKey	TRANSPORT_ENDPOINT	= new AttributeKey(IoSessionUtil.class, "TRANSPORT_ENDPOINT");
	private static final AttributeKey	ENCRYPT_KEY					= new AttributeKey(IoSessionUtil.class, "ENCRYPT_KEY");

	public static void attachEndpointToSession(IoSession session, Endpoint endpoint) {
		session.setAttribute(TRANSPORT_ENDPOINT, endpoint);
	}

	public static Endpoint getEndpointOfSession(IoSession session) {
		return (Endpoint) session.getAttribute(TRANSPORT_ENDPOINT);
	}

	public static void attachEncryptKeyToSession(IoSession session, byte[] encryptKey) {
		session.setAttribute(ENCRYPT_KEY, encryptKey);
	}

	public static byte[] getEncryptKeyOfSession(IoSession session) {
		return (byte[]) session.getAttribute(ENCRYPT_KEY);
	}
}
