/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    DecContext.java
 * Creator:     Archibald.Wang
 * Create-Date: 2011-3-30 下午08:17:26
 *******************************************************************************/
package edu.hziee.common.serialization.bytebean.context;


/**
 * TODO
 * 
 * @author Archibald.Wang
 * @version $Id: DecContext.java 14 2012-01-10 11:54:14Z archie $
 */
public interface DecContext extends FieldCodecContext {
	Object getDecOwner();
	byte[] getDecBytes();
	Class<?> getDecClass();

	DecContextFactory getDecContextFactory();
}
