/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    ByteFieldCodec.java
 * Creator:     Archibald.Wang
 * Create-Date: 2011-3-30 下午08:00:38
 *******************************************************************************/
package edu.hziee.common.serialization.bytebean.codec;

import edu.hziee.common.serialization.bytebean.context.DecContext;
import edu.hziee.common.serialization.bytebean.context.DecResult;
import edu.hziee.common.serialization.bytebean.context.EncContext;

/**
 * 解码噄1�7
 * 
 * @author Archibald.Wang
 * @version $Id: ByteFieldCodec.java 14 2012-01-10 11:54:14Z archie $
 */
public interface ByteFieldCodec {

	FieldCodecCategory getCategory();

	Class<?>[] getFieldType();

	DecResult decode(DecContext ctx);

	byte[] encode(EncContext ctx);
}
