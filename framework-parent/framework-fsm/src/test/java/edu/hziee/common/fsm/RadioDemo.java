/**
 * 
 */
package edu.hziee.common.fsm;

import java.beans.EventHandler;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.hziee.common.fsm.demo.Radio;
import edu.hziee.common.fsm.demo.signal.Next;
import edu.hziee.common.fsm.demo.signal.Pause;
import edu.hziee.common.fsm.demo.signal.Play;
import edu.hziee.common.fsm.demo.signal.Stop;
import edu.hziee.common.tcp.TCPConnector;

/**
 * @author ubuntu-admin
 * 
 */
public class RadioDemo {

  private ApplicationContext ctx;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    ctx = new ClassPathXmlApplicationContext(new String[] { "spring/fsmDemo.xml" });
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    ctx = null;
  }

   /*@Test
  public void test_normal() throws InterruptedException {
    //TCPConnector connector = (TCPConnector) ctx.getBean("connector");

    Play play = new Play();
    Next next = new Next();
    Pause pause = new Pause();
    Stop stop = new Stop();

    next.setIdentification(play.getIdentification());
    pause.setIdentification(play.getIdentification());
    stop.setIdentification(play.getIdentification());

    connector.send(play);
    Thread.sleep(1000L);

    Radio radio = new Radio();

    Assert.assertEquals("play", radio.getStatus());
    Assert.assertEquals(0, radio.getIndex().intValue());

    connector.send(next);
    Thread.sleep(1000L);

    Assert.assertEquals("play", radio.getStatus());
    Assert.assertEquals(1, radio.getIndex().intValue());

    connector.send(next);
    Thread.sleep(1000L);

    Assert.assertEquals("play", radio.getStatus());
    Assert.assertEquals(2, radio.getIndex().intValue());

    connector.send(pause);
    Thread.sleep(1000L);
    Assert.assertEquals("pause", radio.getStatus());
    Assert.assertEquals(2, radio.getIndex().intValue());

    connector.send(stop);
    Thread.sleep(1000L);

    Assert.assertEquals("stop", radio.getStatus());
    Assert.assertEquals(0, radio.getIndex().intValue());
  }*/

}
