/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    LenByteArrayCodec.java
 * Creator:     wangqi
 * Create-Date: 2011-4-27 下午02:27:35
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
 * @version $Id: LenByteArrayCodec.java 14 2012-01-10 11:54:14Z archie $
 */
public class LenByteArrayCodec extends AbstractPrimitiveCodec implements
		ByteFieldCodec {

	private static final Logger logger = LoggerFactory
			.getLogger(LenByteArrayCodec.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taotaosou.common.serialization.bytebean.codec.ByteFieldCodec#decode
	 * (com.taotaosou.common.serialization.bytebean.context.DecContext)
	 */
	@Override
	public DecResult decode(DecContext ctx) {
		// 默认4个字节存储数组长庄1�7
		DecResult ret = ctx.getCodecOf(int.class).decode(
				ctx.getDecContextFactory().createDecContext(ctx.getDecBytes(),
						int.class, ctx.getDecOwner(), null));
		int arrayLength = (Integer) ret.getValue();
		byte[] bytes = ret.getRemainBytes();

		if (bytes.length < arrayLength) {
			String errmsg = "ByteArrayCodec: not enough bytes for decode, need ["
					+ arrayLength + "], actually [" + bytes.length + "].";
			if (null != ctx.getField()) {
				errmsg += "/ cause field is [" + ctx.getField() + "]";
			}
			logger.error(errmsg);
			throw new RuntimeException(errmsg);
		}

		return new DecResult(
				(byte[]) ArrayUtils.subarray(bytes, 0, arrayLength),
				ArrayUtils.subarray(bytes, arrayLength, bytes.length));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taotaosou.common.serialization.bytebean.codec.ByteFieldCodec#encode
	 * (com.taotaosou.common.serialization.bytebean.context.EncContext)
	 */
	@Override
	public byte[] encode(EncContext ctx) {
		byte[] array = (byte[]) ctx.getEncObject();

		// 默认4个字节存储数组长庄1�7
		return (byte[]) ArrayUtils.addAll(
				ctx.getCodecOf(int.class).encode(
						ctx.getEncContextFactory().createEncContext(
								(int) (null == array ? 0 : array.length),
								int.class, null)), array);
	}

	@Override
	public Class<?>[] getFieldType() {
		return new Class<?>[] { byte[].class };
	}

}
