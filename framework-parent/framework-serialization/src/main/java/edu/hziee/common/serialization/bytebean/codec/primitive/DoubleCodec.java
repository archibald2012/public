package edu.hziee.common.serialization.bytebean.codec.primitive;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.serialization.bytebean.codec.ByteFieldCodec;
import edu.hziee.common.serialization.bytebean.codec.NumberCodec;
import edu.hziee.common.serialization.bytebean.context.DecContext;
import edu.hziee.common.serialization.bytebean.context.DecResult;
import edu.hziee.common.serialization.bytebean.context.EncContext;

public class DoubleCodec extends AbstractPrimitiveCodec implements ByteFieldCodec {

	private static final Logger	logger	= LoggerFactory.getLogger(DoubleCodec.class);

	@Override
	public Class<?>[] getFieldType() {
		return new Class<?>[] { double.class, Double.class };
	}

	@Override
	public DecResult decode(DecContext ctx) {
		byte[] bytes = ctx.getDecBytes();
		int byteLength = ctx.getByteSize();
		NumberCodec numberCodec = ctx.getNumberCodec();

		if (byteLength > bytes.length) {
			String errmsg = "DoubleCodec: not enough bytes for decode, need [" + byteLength + "], actually [" + bytes.length
					+ "].";
			if (null != ctx.getField()) {
				errmsg += "/ cause field is [" + ctx.getField() + "]";
			}
			logger.error(errmsg);
			throw new RuntimeException(errmsg);
		}

		return new DecResult(numberCodec.bytes2Double(bytes, byteLength), ArrayUtils.subarray(bytes, byteLength,
				bytes.length));
	}

	@Override
	public byte[] encode(EncContext ctx) {
		double enc = ((Double) ctx.getEncObject()).doubleValue();
		int byteLength = ctx.getByteSize();
		NumberCodec numberCodec = ctx.getNumberCodec();

		return numberCodec.double2Bytes(enc, byteLength);
	}

}
