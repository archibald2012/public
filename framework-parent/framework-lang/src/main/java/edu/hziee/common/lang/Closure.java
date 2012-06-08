
package edu.hziee.common.lang;

import java.util.concurrent.Executor;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: Closure.java 202 2012-03-29 13:41:31Z archie $
 */
public interface Closure {
  /**
   * Performs an action on the specified input object.
   * 
   * @param args
   *          the input to execute on
   * @return
   */
  Object execute(Object... args);

  /**
   * Performs an action in async mode on the specified input object.
   * 
   * @param exec
   * @param args
   */
  void execute(Executor exec, Object... args);
}
