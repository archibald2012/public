/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    BooleanCodec.java
 * Creator:     wangqi
 * Create-Date: 2011-7-12 下午09:03:05
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
 * @version $Id: BooleanCodec.java 14 2012-01-10 11:54:14Z archie $
 */
public class BooleanCodec extends AbstractPrimitiveCodec implements
		ByteFieldCodec {

	private static final Logger logger = LoggerFactory
			.getLogger(BooleanCodec.class);

	@Override
	public Class<?>[] getFieldType() {
		return new Class<?>[] { boolean.class, Boolean.class };
	}

	@Override
	public DecResult decode(DecContext ctx) {
		byte[] bytes = ctx.getDecBytes();
		if (bytes.length < 1) {
			String errmsg = "BooleanCodec: not enough bytes for decode, need [1], actually ["
					+ bytes.length + "].";
			if (null != ctx.getField()) {
				errmsg += "/ cause field is [" + ctx.getField() + "]";
			}
			logger.error(errmsg);
			throw new RuntimeException(errmsg);
		}
		return new DecResult((bytes[0] != 0), ArrayUtils.subarray(bytes, 1,
				bytes.length));
	}

	@Override
	public byte[] encode(EncContext ctx) {
		return ctx.getNumberCodec().short2Bytes(
				(Boolean) ctx.getEncObject() ? (short) 1 : (short) 0, 1);
	}

}
