package edu.hziee.common.tcp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.hziee.common.lang.IpPortPair;
import edu.hziee.common.tcp.bto.NestedBean;
import edu.hziee.common.tcp.bto.SampleReq;

public class TCPRouterTestCase {
  private AbstractApplicationContext ctx;

  private TCPAcceptor                acceptor01 = null;
  private TCPAcceptor                acceptor02 = null;
  private TCPRouter                  router     = null;

  @Before
  public void setUp() throws Exception {
    ctx = new ClassPathXmlApplicationContext(new String[] { "spring/routerDemo.xml" });

    ProtocolCodecFactory codecFactory = (ProtocolCodecFactory) ctx.getBean("codecFactory");

    acceptor01 = new TCPAcceptor();
    acceptor01.setAcceptIp("127.0.0.1");
    acceptor01.setAcceptPort(38888);
    acceptor01.setCodecFactory(codecFactory);
    acceptor01.start();

    acceptor02 = new TCPAcceptor();
    acceptor02.setAcceptIp("127.0.0.1");
    acceptor02.setAcceptPort(39999);
    acceptor02.setCodecFactory(codecFactory);
    acceptor02.start();

    router = new TCPRouter();
    router.setCodecFactory(codecFactory);
  }

  @After
  public void tearDown() throws Exception {
    ctx = null;

    acceptor01.stop();
    acceptor02.stop();
    acceptor01 = null;
    acceptor02 = null;
    router = null;
  }

  @Test
  public void testDoRefresh() throws IOException {

    List<IpPortPair> infos = new ArrayList<IpPortPair>();
    infos.add(new IpPortPair("127.0.0.1", 38888));
    infos.add(new IpPortPair("127.0.0.1", 39999));
    router.doRefreshRoute(infos);

    Assert.assertEquals(2, router.getSnapshot().size());
    Assert.assertEquals(2, router.getConnectors().size());

    infos = new ArrayList<IpPortPair>();
    infos.add(new IpPortPair("127.0.0.1", 38888));
    router.doRefreshRoute(infos);

    Assert.assertEquals(1, router.getSnapshot().size());
    Assert.assertEquals(1, router.getConnectors().size());

    infos = new ArrayList<IpPortPair>();
    infos.add(new IpPortPair("127.0.0.1", 38888));
    infos.add(new IpPortPair("127.0.0.1", 39999));
    router.doRefreshRoute(infos);

    Assert.assertEquals(2, router.getSnapshot().size());
    Assert.assertEquals(2, router.getConnectors().size());
  }

  @Test
  public void testSend() throws IOException {

    SampleReq signal = createSignal();

    router.setHosts("127.0.0.1:38888/127.0.0.1:39999");

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    router.send(signal);
    
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private SampleReq createSignal() {
    SampleReq signal = new SampleReq();
    signal.setIntField(1);
    signal.setShortField((byte) 1);
    signal.setByteField((byte) 1);
    signal.setLongField(1L);
    signal.setStringField("中文");

    signal.setByteArrayField(new byte[] { 127 });

    ArrayList<NestedBean> listField = new ArrayList<NestedBean>();
    NestedBean bean1 = new NestedBean();
    bean1.setIntField(1);
    bean1.setShortField((short) 1);
    listField.add(bean1);
    NestedBean bean2 = new NestedBean();
    bean2.setIntField(2);
    bean2.setShortField((short) 2);
    listField.add(bean2);
    signal.setListField(listField);

    NestedBean bean3 = new NestedBean();
    bean3.setIntField(3);
    bean3.setShortField((short) 3);
    signal.setBeanField(bean3);
    return signal;
  }
}
