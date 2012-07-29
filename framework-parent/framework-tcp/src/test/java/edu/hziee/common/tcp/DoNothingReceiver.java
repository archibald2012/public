package edu.hziee.common.tcp;

import edu.hziee.common.lang.transport.Receiver;
import edu.hziee.common.lang.transport.Sender;
import edu.hziee.common.tcp.bto.SampleResp;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: DoNothingReceiver.java 14 2012-01-10 11:54:14Z archie $
 */
public class DoNothingReceiver implements Receiver {

	public void messageReceived(Object msg) {
		Sender sender = TransportUtil.getSenderOf(msg);
		SampleResp resp = new SampleResp();
		resp.setIntField(1);
		sender.send(resp);
	}

}
