/**
 * 
 */
package edu.hziee.common.event.closure;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.hziee.common.event.EventBus;
import edu.hziee.common.event.SampleSignal;
import edu.hziee.common.lang.Closure;

/**
 * @author ubuntu-admin
 * 
 */
public class EventBusDispatcherTestCase {

  EventBusDispatcher eventDispatcher;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    eventDispatcher = new EventBusDispatcher();
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    eventDispatcher = null;
  }

  @Test
  public void testEvaluate() throws InterruptedException {
    Assert.assertFalse(eventDispatcher.evaluate(null));

    final SampleSignal signal = new SampleSignal();
    signal.setIntField(100);
    Assert.assertFalse(eventDispatcher.evaluate(signal));

    Collection<Class<?>> allowTypes = new ArrayList<Class<?>>();
    allowTypes.add(SampleSignal.class);
    eventDispatcher.setAllowTypes(allowTypes);
    Assert.assertTrue(eventDispatcher.evaluate(signal));

    eventDispatcher.setEventBus(new EventBus() {
      @Override
      public void subscribe(String event, Closure closure) {
      }

      @Override
      public void unsubscribe(String event, Closure closure) {
      }

      @Override
      public void publish(String event, Object... args) {
        Assert.assertEquals(signal, args[0]);
      }
    });
    eventDispatcher.messageReceived(signal);
    Thread.sleep(1000);

    Assert.assertFalse(eventDispatcher.evaluate(new NotAllowedType()));
  }

  private class NotAllowedType {

  }
}
