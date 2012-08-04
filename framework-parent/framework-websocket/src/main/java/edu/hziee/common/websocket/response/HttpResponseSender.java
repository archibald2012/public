package edu.hziee.common.websocket.response;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: HttpResponseSender.java 4 2012-01-10 11:51:54Z archie $
 */
public interface HttpResponseSender {

	/**
	 * 
	 * @param channel
	 * @param response
	 */
	void sendResponse(Channel channel, HttpResponse response);

	/**
	 * 
	 * @param channel
	 * @param httpResponseStatus
	 * @param responseContent
	 */
	void sendResponse(Channel channel, HttpResponseStatus httpResponseStatus);

	/**
	 * 
	 * @param channel
	 * @param httpResponseStatus
	 * @param responseContent
	 */
	void sendResponse(Channel channel, HttpResponseStatus httpResponseStatus, String responseContent);

	/**
	 * 
	 * @param channel
	 * @param httpResponseStatus
	 * @param responseContent
	 * @param charsetName
	 * @param contentType
	 */
	void sendResponse(Channel channel, HttpResponseStatus httpResponseStatus, String responseContent, String charsetName,
			String contentType);

	/**
	 * 
	 * @param channel
	 * @param httpResponseStatus
	 * @param responseContent
	 * @param charsetName
	 */
	void sendResponse(Channel channel, HttpResponseStatus httpResponseStatus, String responseContent, String charsetName);

	/**
	 * 
	 * @param channel
	 * @param redirectUrl
	 */
	void sendRedirectResponse(Channel channel, String redirectUrl);

	/**
	 * 
	 * @param channel
	 * @param fullContent
	 * @param startPos
	 * @param endPos
	 * @return content-range
	 */
	String sendFile(Channel channel, byte[] fullContent, int startPos, int endPos);
}
