/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    AbstractPrimitiveCodec.java
 * Creator:     wangqi
 * Create-Date: 2011-4-27 下午01:40:44
 *******************************************************************************/
package edu.hziee.common.serialization.bytebean.codec.primitive;

import edu.hziee.common.serialization.bytebean.codec.ByteFieldCodec;
import edu.hziee.common.serialization.bytebean.codec.FieldCodecCategory;


/**
 * 基本类型解码噄1�7
 * 
 * @author wangqi
 * @version $Id: AbstractPrimitiveCodec.java 14 2012-01-10 11:54:14Z archie $
 */
public abstract class AbstractPrimitiveCodec implements ByteFieldCodec {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taotaosou.common.serialization.bytebean.field.ByteFieldCodec#getCategory
	 * ()
	 */
	@Override
	public FieldCodecCategory getCategory() {
		return null;
	}

}
