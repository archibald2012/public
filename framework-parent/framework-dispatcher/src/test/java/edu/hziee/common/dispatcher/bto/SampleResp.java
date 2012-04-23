/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    SampleResp.java
 * Creator:     wangqi
 * Create-Date: 2011-6-15 上午09:52:36
 *******************************************************************************/
package edu.hziee.common.dispatcher.bto;

import edu.hziee.common.serialization.bytebean.annotation.ByteField;
import edu.hziee.common.serialization.protocol.annotation.SignalCode;
import edu.hziee.common.serialization.protocol.xip.AbstractXipSignal;
import edu.hziee.common.serialization.protocol.xip.XipResponse;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: SampleResp.java 14 2012-01-10 11:54:14Z archie $
 */
@SignalCode(messageCode = 100002)
public class SampleResp extends AbstractXipSignal implements XipResponse {
	
	@ByteField(index = 0)
	private int intField;

	public int getIntField() {
		return intField;
	}

	public void setIntField(int intField) {
		this.intField = intField;
	}

}
