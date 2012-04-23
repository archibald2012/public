package edu.hziee.common.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.transport.Receiver;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: EchoMessageClosure.java 4 2012-01-10 11:51:54Z archie $
 */
public class EchoMessageClosure implements Receiver {

  private static final Logger logger         = LoggerFactory.getLogger(EchoMessageClosure.class);

  @Override
  public void messageReceived(Object input) {
    logger.debug("try echo:" + input);

  }

}
