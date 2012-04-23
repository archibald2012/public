/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    AbstractCategoryCodec.java
 * Creator:     wangqi
 * Create-Date: 2011-4-27 下午01:39:37
 *******************************************************************************/
package edu.hziee.common.serialization.bytebean.codec;


/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: AbstractCategoryCodec.java 14 2012-01-10 11:54:14Z archie $
 */
public abstract class AbstractCategoryCodec implements ByteFieldCodec {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taotaosou.common.serialization.bytebean.field.ByteFieldCodec#getFieldType
	 * ()
	 */
	@Override
	public Class<?>[] getFieldType() {
		return null;
	}

}
