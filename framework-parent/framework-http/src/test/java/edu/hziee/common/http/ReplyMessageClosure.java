package edu.hziee.common.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.http.response.DefaultHttpResponseSender;
import edu.hziee.common.http.response.HttpResponseSender;
import edu.hziee.common.lang.transport.Receiver;
import edu.hziee.common.lang.transport.Sender;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: ReplyMessageClosure.java 4 2012-01-10 11:51:54Z archie $
 */
public class ReplyMessageClosure implements Receiver {

  private static final Logger logger         = LoggerFactory.getLogger(ReplyMessageClosure.class);

  private HttpResponseSender  responseHelper = new DefaultHttpResponseSender();

  @Override
  public void messageReceived(Object input) {
    logger.debug("try echo:" + input);
    if (responseHelper != null) {
      Sender sender = TransportUtil.getSenderOf(input);
      sender.send(input);
    }
  }

}
