/**
 * 
 */
package edu.hziee.common.event.subscriber;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.hziee.common.event.DefaultEventBus;
import edu.hziee.common.event.SampleEventUnit;
import edu.hziee.common.event.Subscription;
import edu.hziee.common.lang.Functor;

/**
 * @author ubuntu-admin
 * 
 */
public class MultiEventSubscriberTestCase {

  private static final String EVENT_INCREASE        = "increase";
  private static final String EVENT_INCREASE_BYSTEP = "increaseByStep";

  DefaultEventBus             eventBus              = null;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    eventBus = new DefaultEventBus();
    eventBus.start();
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    eventBus.destroy();
    eventBus = null;
  }

  @Test
  public void testSubscribe() throws InterruptedException {
    SampleEventUnit target = new SampleEventUnit();

    MultiEventSubscriber subscriber = new MultiEventSubscriber(eventBus);

    List<Subscription> subs = new ArrayList<Subscription>();

    Subscription s = new Subscription(EVENT_INCREASE);
    Functor functor = new Functor(target, "increase");
    Functor functor2 = new Functor(target, "increase2");
    s.addClosure(functor);
    s.addClosure(functor2);
    subs.add(s);

    Subscription s2 = new Subscription(EVENT_INCREASE_BYSTEP);
    Functor functor3 = new Functor(target, "increaseByStep");
    s2.addClosure(functor3);
    subs.add(s2);

    subscriber.setSubscriptions(subs);

    subscriber.start();

    eventBus.publish(EVENT_INCREASE);

    Thread.sleep(1000);

    Assert.assertEquals(2, target.getCount().intValue());

    eventBus.publish(EVENT_INCREASE);

    Thread.sleep(1000);

    Assert.assertEquals(4, target.getCount().intValue());

    eventBus.publish(EVENT_INCREASE_BYSTEP, 20);

    Thread.sleep(1000);

    Assert.assertEquals(24, target.getCount().intValue());

    subscriber.unsubscribe(EVENT_INCREASE_BYSTEP, functor3);

    eventBus.publish(EVENT_INCREASE_BYSTEP, 20);

    Thread.sleep(1000);

    Assert.assertEquals(24, target.getCount().intValue());
  }
}
