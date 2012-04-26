package edu.hziee.common.http.reactor;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import edu.hziee.common.http.response.DefaultHttpResponseSender;
import edu.hziee.common.http.response.HttpResponseSender;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: SendConstantResponseReactor.java 27 2011-09-24 14:23:42Z archie
 *          $
 */
public class ConstantResponseReactor implements HttpReactor {

  private HttpReactor        nextReactor    = null;
  private HttpResponse       response;
  private HttpResponseSender responseSender = new DefaultHttpResponseSender();

  public ConstantResponseReactor(HttpResponse response) {
    this.response = response;
  }

  public HttpResponse getResponse() {
    return response;
  }

  public HttpResponseSender getResponseSender() {
    return responseSender;
  }

  public void setResponseSender(HttpResponseSender responseSender) {
    this.responseSender = responseSender;
  }

  public HttpReactor getNextReactor() {
    return nextReactor;
  }

  public void setNextReactor(HttpReactor nextReactor) {
    this.nextReactor = nextReactor;
  }

  public void onHttpRequest(Channel channel, HttpRequest request) {

    String uuid = request.getHeader("uuid");
    if (uuid != null) {
      response.setHeader("uuid", uuid);
    }
    responseSender.sendResponse(channel, response);

    if (null != this.nextReactor) {
      nextReactor.onHttpRequest(null, request);
    }
  }

}
