package edu.hziee.common.event.closure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.event.EventBus;
import edu.hziee.common.lang.PackageUtil;
import edu.hziee.common.lang.transport.Receiver;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: EventDispatchClosure.java 332 2011-06-13 08:17:44Z archie.wang
 *          $
 */
public class EventBusDispatcher implements Receiver {

  private static final Logger  logger     = LoggerFactory.getLogger(EventBusDispatcher.class);

  private EventBus             eventBus;

  private Collection<Class<?>> allowTypes = new ArrayList<Class<?>>();

  /**
   * @return the allowTypes
   */
  public Collection<Class<?>> getAllowTypes() {
    return allowTypes;
  }

  /**
   * @param allowTypes
   *          the allowTypes to set
   */
  public void setAllowTypes(Collection<Class<?>> allowTypes) {
    this.allowTypes.clear();
    this.allowTypes.addAll(allowTypes);
  }

  public void setAllowPackages(Collection<String> packages) {
    this.allowTypes.clear();

    if (null != packages) {
      for (String pkgName : packages) {
        try {
          String[] clsNames = PackageUtil.findClassesInPackage(pkgName, null, null);
          for (String clsName : clsNames) {
            try {
              ClassLoader cl = Thread.currentThread().getContextClassLoader();
              if (logger.isDebugEnabled()) {
                logger.debug("using ClassLoader {" + cl + "} to load Class {" + clsName + "}");
              }
              Class<?> cls = cl.loadClass(clsName);
              this.allowTypes.add(cls);
            } catch (ClassNotFoundException e) {
              logger.error("setAllowPackages: ", e);
            }
          }
        } catch (IOException e) {
          logger.error("setAllowPackages: ", e);
        }
      }
    }
  }

  public void messageReceived(Object symbol) {
    if (!evaluate(symbol)) {
      if (logger.isDebugEnabled()) {
        logger.debug("symbol [" + symbol + "] not acceptable, just ignore");
      }
      return;
    }

    String event = symbol.getClass().getName();
    if (logger.isDebugEnabled()) {
      logger.debug("publish event [" + event + "] <--> " + symbol);
    }

    eventBus.publish(event, symbol);
  }

  boolean evaluate(Object object) {
    if (null == object) {
      return false;
    }
    return allowTypes.contains(object.getClass());
  }

  public EventBus getEventBus() {
    return eventBus;
  }

  public void setEventBus(EventBus eventBus) {
    this.eventBus = eventBus;
  }

}
