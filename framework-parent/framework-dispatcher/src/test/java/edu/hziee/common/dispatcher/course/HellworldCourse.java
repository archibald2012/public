
package edu.hziee.common.dispatcher.course;

import edu.hziee.common.dispatcher.bto.SampleReq;
import edu.hziee.common.dispatcher.bto.SampleResp;
import edu.hziee.common.lang.transport.Sender;
import edu.hziee.common.lang.transport.TransportUtil;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: HellworldCourse.java 14 2012-01-10 11:54:14Z archie $
 */
public class HellworldCourse implements BusinessCourse {

	@BizMethod
	public void onSampleSingal(SampleReq req) {
		System.out.println("HellowordCourse received req: " + req);

		SampleResp resp = new SampleResp();
		resp.setIdentification(req.getIdentification());
		resp.setIntField(req.getIntField());

		Sender sender = TransportUtil.getSenderOf(req);

		sender.send(resp);
	}
}
