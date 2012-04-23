/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    DecContextFactory.java
 * Creator:     Archibald.Wang
 * Create-Date: 2011-3-30 下午08:01:24
 *******************************************************************************/
package edu.hziee.common.serialization.bytebean.context;

import edu.hziee.common.serialization.bytebean.field.ByteFieldDesc;

/**
 * TODO
 * 
 * @author Archibald.Wang
 * @version $Id: DecContextFactory.java 14 2012-01-10 11:54:14Z archie $
 */
public interface DecContextFactory {
	DecContext createDecContext(byte[] decBytes, Class<?> targetType, Object parent,
			ByteFieldDesc desc);
}
