
package edu.hziee.common.serialization.bytebean.codec.primitive;

import java.io.UnsupportedEncodingException;

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
 * @version $Id: CStyleStringCodec.java 14 2012-01-10 11:54:14Z archie $
 */
public class CStyleStringCodec extends AbstractPrimitiveCodec implements
		ByteFieldCodec {

	private static final Logger logger = LoggerFactory
			.getLogger(CStyleStringCodec.class);
	private static final String XIP_STR_CHARSET = "UTF-8";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taotaosou.common.serialization.bytebean.codec.ByteFieldCodec#getFieldType
	 * ()
	 */
	@Override
	public Class<?>[] getFieldType() {
		return new Class<?>[] { String.class };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taotaosou.common.serialization.bytebean.codec.ByteFieldCodec#decode
	 * (com.taotaosou.common.serialization.bytebean.context.DecContext)
	 */
	@Override
	public DecResult decode(DecContext ctx) {
		byte[] bytes = ctx.getDecBytes();
		Object ret = null;

		// 浠�00缁撳熬
		int index = ArrayUtils.indexOf(bytes, (byte) 0x00);
		if (-1 == index) {
			String errmsg = "CStyleString: could not found \\0 for string terminated.";
			if (null != ctx.getField()) {
				errmsg += "/ cause field is [" + ctx.getField() + "]";
			}
			logger.error(errmsg);
			throw new RuntimeException(errmsg);
		}

		try {
			byte[] tmp = ArrayUtils.subarray(bytes, 0, index);
			ret = new String(tmp, XIP_STR_CHARSET);
		} catch (UnsupportedEncodingException e) {
			logger.error("CStyleString", e);
		}

		return new DecResult(ret, ArrayUtils.subarray(bytes, index + 1,
				bytes.length));
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
		String value = (String) ctx.getEncObject();

		byte[] bytes = null;

		if (null == value) {
			return new byte[] { 0 };
		}

		try {
			bytes = value.getBytes(XIP_STR_CHARSET);
		} catch (UnsupportedEncodingException e) {
			logger.error("CStyleString", e);
		}

		// 浠�00缁撳熬
		return ArrayUtils.add(bytes, (byte) 0);
	}

}
