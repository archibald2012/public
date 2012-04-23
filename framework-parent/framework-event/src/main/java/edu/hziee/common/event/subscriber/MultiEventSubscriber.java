/**
 * 
 */
package edu.hziee.common.event.subscriber;

import java.util.ArrayList;
import java.util.Collection;

import edu.hziee.common.event.EventBus;
import edu.hziee.common.event.Subscription;
import edu.hziee.common.lang.Closure;
import edu.hziee.common.lang.Functor;

/**
 * @author ubuntu-admin
 * 
 */
public class MultiEventSubscriber extends EventSubscriber {

  private Collection<Subscription> subscriptions = new ArrayList<Subscription>();

  public MultiEventSubscriber(EventBus ebus) {
    super(ebus);
  }

  public void setSubscriptions(Collection<Subscription> subscriptions) {
    this.subscriptions.clear();
    this.subscriptions.addAll(subscriptions);
  }

  public void setEvents(Collection<String> events, Functor functor) {
    this.subscriptions.clear();
    for (String event : events) {
      Subscription sub = new Subscription(event);
      sub.addClosure(functor);
      this.subscriptions.add(sub);
    }
  }

  public void start() {
    for (Subscription s : this.subscriptions) {
      String event = s.getEvent();
      for (Closure f : s.getClosures()) {
        subscribe(event, f);
      }
    }
  }

}
