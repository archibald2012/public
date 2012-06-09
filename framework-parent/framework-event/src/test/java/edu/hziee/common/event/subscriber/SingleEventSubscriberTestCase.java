/**
 * 
 */
package edu.hziee.common.event.subscriber;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.hziee.common.event.DefaultEventBus;
import edu.hziee.common.event.SampleEventUnit;

/**
 * @author ubuntu-admin
 * 
 */
public class SingleEventSubscriberTestCase {

  DefaultEventBus eventBus = null;

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
    /*SampleEventUnit target = new SampleEventUnit();

    SingleEventSubscriber subscriber = new SingleEventSubscriber(eventBus, "EventSubscriber");

    subscriber.setEvent("event.ben.sample");
    subscriber.setTarget(target);
    subscriber.setMethod("increase");

    subscriber.start();

    eventBus.publish("event.ben.sample", new Object[] {});

    Thread.sleep(1000);

    Assert.assertEquals(1, target.getCount().intValue());

    eventBus.publish("event.ben.sample", new Object[] {});

    Thread.sleep(1000);

    Assert.assertEquals(2, target.getCount().intValue());

    subscriber.destroy();

    eventBus.publish("event.ben.sample", new Object[] {});

    Thread.sleep(1000);

    Assert.assertEquals(2, target.getCount().intValue());*/
  }

}
