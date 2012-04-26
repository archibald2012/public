
package edu.hziee.common.serialization.bytebean.codec.primitive;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.serialization.bytebean.codec.ByteFieldCodec;
import edu.hziee.common.serialization.bytebean.codec.NumberCodec;
import edu.hziee.common.serialization.bytebean.context.DecContext;
import edu.hziee.common.serialization.bytebean.context.DecResult;
import edu.hziee.common.serialization.bytebean.context.EncContext;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: LongCodec.java 14 2012-01-10 11:54:14Z archie $
 */
public class LongCodec extends AbstractPrimitiveCodec implements ByteFieldCodec {

	private static final Logger logger = LoggerFactory.getLogger(LongCodec.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taotaosou.common.serialization.bytebean.field.ByteFieldCodec#getFieldType
	 * ()
	 */
	@Override
	public Class<?>[] getFieldType() {
		return new Class<?>[] { long.class, Long.class };
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
		int byteLength = ctx.getByteSize();
		NumberCodec numberCodec = ctx.getNumberCodec();

		if (byteLength > bytes.length) {
			String errmsg = "LongCodec: not enough bytes for decode, need ["
					+ byteLength + "], actually [" + bytes.length + "].";
			if (null != ctx.getField()) {
				errmsg += "/ cause field is [" + ctx.getField() + "]";
			}
			logger.error(errmsg);
			throw new RuntimeException(errmsg);
		}

		return new DecResult(numberCodec.bytes2Long(bytes, byteLength),
				ArrayUtils.subarray(bytes, byteLength, bytes.length));
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
		long enc = ((Long) ctx.getEncObject()).longValue();
		int byteLength = ctx.getByteSize();
		NumberCodec numberCodec = ctx.getNumberCodec();

		return numberCodec.long2Bytes(enc, byteLength);
	}

}
