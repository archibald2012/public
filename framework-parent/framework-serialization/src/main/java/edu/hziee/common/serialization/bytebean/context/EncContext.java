/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    EncContext.java
 * Creator:     Archibald.Wang
 * Create-Date: 2011-3-30 下午08:25:49
 *******************************************************************************/
package edu.hziee.common.serialization.bytebean.context;


/**
 * TODO
 * 
 * @author Archibald.Wang
 * @version $Id: EncContext.java 14 2012-01-10 11:54:14Z archie $
 */
public interface EncContext extends FieldCodecContext {
	Object getEncObject();
	Class<?> getEncClass();

	EncContextFactory getEncContextFactory();
}
