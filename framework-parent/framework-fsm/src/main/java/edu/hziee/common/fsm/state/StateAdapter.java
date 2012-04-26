/**
 * 
 */
package edu.hziee.common.fsm.state;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.fsm.State;
import edu.hziee.common.lang.ClassUtil;

/**
 * @author ubuntu-admin
 * 
 */
public class StateAdapter<T> implements State<T> {

  private static final Logger logger           = LoggerFactory.getLogger(StateAdapter.class);

  private String              name;
  private Object              target;

  private Method              methodOfEnter    = null;
  private Method              methodOfExit     = null;
  private Map<String, Method> methodsOfExecute = new HashMap<String, Method>();

  @Override
  public String enter(T t) {
    try {
      if (null != this.methodOfEnter) {
        if (logger.isTraceEnabled()) {
          logger.trace("enter [{}]", name);
        }
        Object ret = this.methodOfEnter.invoke(target, t);
        if (ret != null) {
          return ClassUtil.getSimpleName((Class<?>) ret);
        }
      }
    } catch (Exception e) {
      logger.error("enter:", e);
    }
    return null;
  }

  @Override
  public String execute(T t, String event, Object... args) {
    if (null == event) {
      logger.warn("event is null.");
      return null;
    }

    try {
      Method m = getMethodOfExecute(event);
      if (null != m) {
        Object ret = m.invoke(target, ArrayUtils.add(args, 0, t));
        if (ret != null) {
          return ClassUtil.getSimpleName((Class<?>) ret);
        }
      } else {
        logger.warn("received unexpected event [{}], in state [{}]", event, name);
      }
    } catch (Exception e) {
      logger.error("execute:", e);
    }
    return null;
  }
  @Override
  public void exit(T t) {
    try {
      if (null != this.methodOfExit) {
        if (logger.isTraceEnabled()) {
          logger.trace("exit [{}]", name);
        }
        this.methodOfExit.invoke(target, t);
      }
    } catch (Exception e) {
      logger.error("exit:", e);
    }
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(Class<?> c) {
    this.name = ClassUtil.getSimpleName(c);
  }

  /**
   * @return the methodOfEnter
   */
  public Method getMethodOfEnter() {
    return methodOfEnter;
  }

  /**
   * @param methodOfEnter
   *          the methodOfEnter to set
   */
  public void setMethodOfEnter(Method methodOfEnter) {
    if (null != this.methodOfEnter) {
      throw new RuntimeException("more than one accept method.");
    }
    this.methodOfEnter = methodOfEnter;
    this.methodOfEnter.setAccessible(true);
  }

  public Method getMethodOfExit() {
    return methodOfExit;
  }

  public void setMethodOfExit(Method methodOfExit) {
    if (null != this.methodOfExit) {
      throw new RuntimeException("more than one exit method.");
    }
    this.methodOfExit = methodOfExit;
    this.methodOfExit.setAccessible(true);
  }

  /**
   * @return the target
   */
  public Object getTarget() {
    return target;
  }

  /**
   * @param target
   *          the target to set
   */
  public void setTarget(Object target) {
    this.target = target;
  }

  public void addMethodOfExecute(String event, Method methodOfExecute) {
    methodOfExecute.setAccessible(true);
    if (null != this.methodsOfExecute.get(event)) {
      throw new RuntimeException("more than one execute method for event [" + event + "].");
    }
    this.methodsOfExecute.put(event, methodOfExecute);
  }

  public String[] getAcceptedEvents() {
    return this.methodsOfExecute.keySet().toArray(new String[0]);
  }

  public Method getMethodOfExecute(String event) {
    return this.methodsOfExecute.get(event);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return name;
  }

}
