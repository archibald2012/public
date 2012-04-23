/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    FieldCodecProvider.java
 * Creator:     Archibald.Wang
 * Create-Date: 2011-3-30 下午07:59:55
 *******************************************************************************/
package edu.hziee.common.serialization.bytebean.codec;



/**
 * 字段解码器提供商
 * 
 * @author Archibald.Wang
 * @version $Id: FieldCodecProvider.java 14 2012-01-10 11:54:14Z archie $
 */
public interface FieldCodecProvider {
	ByteFieldCodec getCodecOf(FieldCodecCategory type);
	ByteFieldCodec getCodecOf(Class<?> clazz);
}
