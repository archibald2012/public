/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    HellworldCourse.java
 * Creator:     wangqi
 * Create-Date: 2011-6-15 上午09:46:06
 *******************************************************************************/
package edu.hziee.common.dispatcher.course;

import edu.hziee.common.dispatcher.bto.SampleReq;
import edu.hziee.common.dispatcher.bto.SampleResp;
import edu.hziee.common.lang.transport.Sender;
import edu.hziee.common.tcp.TransportUtil;

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
