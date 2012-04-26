/**
 * 
 */
package edu.hziee.common.event;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.hziee.common.tcp.TCPConnector;

/**
 * @author ubuntu-admin
 * 
 */
public class SingleEventDemo {

  private ApplicationContext ctx;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    ctx = new ClassPathXmlApplicationContext(new String[] { "spring/singleEventDemo.xml" });
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
    ctx = null;
  }

  @Test
  public void test() throws InterruptedException {
    TCPConnector connector = (TCPConnector) ctx.getBean("connector");

    SampleSignal signal = new SampleSignal();
    signal.setIntField(100);
    
    Thread.sleep(1000L);
    
    connector.send(signal);

    Thread.sleep(2000L);

    SampleEventUnit eventUnit = (SampleEventUnit) ctx.getBean("sampleEventUnit");

    Assert.assertEquals(100, eventUnit.getCount().intValue());
  }

}
