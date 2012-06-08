/**
 * 
 */
package edu.hziee.common.lang.transport;

import java.util.concurrent.TimeUnit;

/**
 * @author C3China
 * 
 */
public interface SenderSync {

  Object sendAndWait(Object bean);

  Object sendAndWait(Object bean, long duration, TimeUnit units);
}
