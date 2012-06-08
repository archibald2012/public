/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    SampleBean.java
 * Creator:     wangqi
 * Create-Date: 2011-4-27 下午03:46:48
 *******************************************************************************/
package edu.hziee.common.fsm.demo.signal;

import edu.hziee.common.serialization.protocol.annotation.SignalCode;
import edu.hziee.common.serialization.protocol.xip.AbstractXipSignal;
import edu.hziee.common.serialization.protocol.xip.XipRequest;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: Next.java 14 2012-01-10 11:54:14Z archie $
 */
@SignalCode(messageCode = 0x122)
public class Next extends AbstractXipSignal implements XipRequest {

}
