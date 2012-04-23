/**
 * 
 */
package edu.hziee.common.event.subscriber;

import edu.hziee.common.event.EventBus;
import edu.hziee.common.lang.Functor;

/**
 * @author ubuntu-admin
 * 
 */
public class SingleEventSubscriber extends EventSubscriber {

  private Object target = null;
  private String event  = null;
  private String method = null;

  public SingleEventSubscriber(EventBus ebus) {
    super(ebus);
  }

  public void start() {
    subscribe(event, new Functor(getTarget(), getMethod()));
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public Object getTarget() {
    return target;
  }

  public void setTarget(Object target) {
    this.target = target;
  }

  /**
   * @param event
   *          the event to set
   */
  public void setEvent(String event) {
    this.event = event.trim();
  }

  public String getEvent() {
    return event;
  }
}
