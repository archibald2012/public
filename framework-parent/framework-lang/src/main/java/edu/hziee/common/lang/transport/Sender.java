
package edu.hziee.common.lang.transport;


/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: Sender.java 28 2012-01-13 03:02:20Z archie $
 */
public interface Sender {

  void send(Object bean);
  
  void send(Object object, Receiver receiver);

}
