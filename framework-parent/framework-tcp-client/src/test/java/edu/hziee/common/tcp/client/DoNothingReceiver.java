package edu.hziee.common.tcp.client;

import edu.hziee.common.lang.transport.Receiver;
import edu.hziee.common.lang.transport.Sender;
import edu.hziee.common.tcp.TransportUtil;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: DoNothingReceiver.java 14 2012-01-10 11:54:14Z archie $
 */
public class DoNothingReceiver implements Receiver {

	public void messageReceived(Object msg) {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Sender sender = TransportUtil.getSenderOf(msg);
		SampleResp resp = new SampleResp();
		resp.setIdentification(((SampleReq) msg).getIdentification());
		sender.send(resp);
	}

}
