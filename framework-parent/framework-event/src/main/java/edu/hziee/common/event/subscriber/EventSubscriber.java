/**
 * 
 */
package edu.hziee.common.event.subscriber;

import edu.hziee.common.event.EventBus;
import edu.hziee.common.lang.Closure;

/**
 * @author ubuntu-admin
 * 
 */
public class EventSubscriber {

  private EventBus ebus = null;

  public EventSubscriber(EventBus ebus) {
    this.ebus = ebus;
  }

  /**
   * Subscribe to an event with async callback.
   * 
   * @param event
   *          The event type
   * @param closure
   *          The callback function to be invoked when a event of type 'etype'
   *          is published onto the bus.
   */
  protected void subscribe(String event, Closure closure) {
    ebus.subscribe(event, closure);
  }

  /**
   * Unsubscribe from the bus for the event type.
   * 
   * @param event
   * @param closure
   */
  protected void unsubscribe(String event, Closure closure) {
    ebus.unsubscribe(event, closure);
  }

  protected EventBus getEventBus() {
    return ebus;
  }

}
