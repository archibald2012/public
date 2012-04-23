/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    ByteCodec.java
 * Creator:     wangqi
 * Create-Date: 2011-4-27 下午12:46:30
 *******************************************************************************/
package edu.hziee.common.serialization.bytebean.codec.primitive;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.serialization.bytebean.codec.ByteFieldCodec;
import edu.hziee.common.serialization.bytebean.context.DecContext;
import edu.hziee.common.serialization.bytebean.context.DecResult;
import edu.hziee.common.serialization.bytebean.context.EncContext;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: ByteCodec.java 14 2012-01-10 11:54:14Z archie $
 */
public class ByteCodec extends AbstractPrimitiveCodec implements ByteFieldCodec {

	private static final Logger logger = LoggerFactory.getLogger(ByteCodec.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taotaosou.common.serialization.bytebean.field.ByteFieldCodec#getFieldType
	 * ()
	 */
	@Override
	public Class<?>[] getFieldType() {
		return new Class<?>[] { byte.class, Byte.class };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taotaosou.common.serialization.bytebean.field.ByteFieldCodec#decode
	 * (com.taotaosou.common.serialization.bytebean.context.DecContext)
	 */
	@Override
	public DecResult decode(DecContext ctx) {
		byte[] bytes = ctx.getDecBytes();
		if (bytes.length < 1) {
			String errmsg = "ByteCodec: not enough bytes for decode, need [1], actually ["
					+ bytes.length + "].";
			if (null != ctx.getField()) {
				errmsg += "/ cause field is [" + ctx.getField() + "]";
			}
			logger.error(errmsg);
			throw new RuntimeException(errmsg);
		}
		return new DecResult(bytes[0], ArrayUtils.subarray(bytes, 1,
				bytes.length));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taotaosou.common.serialization.bytebean.field.ByteFieldCodec#encode
	 * (com.taotaosou.common.serialization.bytebean.context.EncContext)
	 */
	@Override
	public byte[] encode(EncContext ctx) {
		return new byte[] { ((Byte) ctx.getEncObject()).byteValue() };
	}

}
