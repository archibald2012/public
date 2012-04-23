/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    SsipSignal.java
 * Creator:     wangqi
 * Create-Date: 2011-4-28 上午10:54:39
 *******************************************************************************/
package edu.hziee.common.serialization.protocol.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: SignalCode.java 14 2012-01-10 11:54:14Z archie $
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SignalCode {
	int messageCode();
}
