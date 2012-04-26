package edu.hziee.common.http.reactor;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: HttpReactor.java 4 2012-01-10 11:51:54Z archie $
 */
public interface HttpReactor {
  void onHttpRequest(Channel channel, HttpRequest request);
}
