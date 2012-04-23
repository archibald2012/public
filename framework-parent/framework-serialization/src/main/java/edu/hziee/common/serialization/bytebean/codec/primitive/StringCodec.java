/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    StringCodec.java
 * Creator:     wangqi
 * Create-Date: 2011-4-27 下午01:14:12
 *******************************************************************************/
package edu.hziee.common.serialization.bytebean.codec.primitive;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.serialization.bytebean.codec.ByteFieldCodec;
import edu.hziee.common.serialization.bytebean.codec.NumberCodec;
import edu.hziee.common.serialization.bytebean.context.DecContext;
import edu.hziee.common.serialization.bytebean.context.DecResult;
import edu.hziee.common.serialization.bytebean.context.EncContext;
import edu.hziee.common.serialization.bytebean.field.ByteFieldDesc;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: StringCodec.java 14 2012-01-10 11:54:14Z archie $
 */
public class StringCodec extends AbstractPrimitiveCodec implements
		ByteFieldCodec {

	private static final Logger logger = LoggerFactory.getLogger(StringCodec.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.taotaosou.common.serialization.bytebean.field.ByteFieldCodec#getFieldType
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
	 * com.taotaosou.common.serialization.bytebean.field.ByteFieldCodec#decode
	 * (com.taotaosou.common.serialization.bytebean.context.DecContext)
	 */
	@Override
	public DecResult decode(DecContext ctx) {
		byte[] bytes = ctx.getDecBytes();
		ByteFieldDesc desc = ctx.getFieldDesc();
		if (null == desc) {
			throw new RuntimeException("StringCodec: ByteFieldDesc is null");
		}

		int length = desc.getFixedLength();

		boolean needFilter = false;
		if (length > 0)
			needFilter = true;

		if (length < 0) {
			length = desc.getStringLengthInBytes(ctx.getDecOwner());
		}

		if (length < 0) {
			throw new RuntimeException("StringCodec: length < 0");
		}
		NumberCodec numberCodec = ctx.getNumberCodec();

		String charset = numberCodec.convertCharset(desc.getCharset());

		String ret = null;

		if (length > bytes.length) {
			String errmsg = "StringCodec: not enough bytes for decode, need ["
					+ length + "], actually [" + bytes.length + "].";
			if (null != ctx.getField()) {
				errmsg += "/ cause field is [" + ctx.getField() + "]";
			}
			logger.error(errmsg);
			throw new RuntimeException(errmsg);
		}

		if (length > 0) {
			try {
				byte[] values = ArrayUtils.subarray(bytes, 0, length);

				if (needFilter && charset.startsWith("UTF-16")) {
					int len = values.length;
					for (; len > 0;) {
						if ((values[len - 1] & 0xFF) == 0) {
							len -= 2;
						} else
							break;
					}
					values = ArrayUtils.subarray(values, 0, len);
				}

				ret = new String(values, charset);
			} catch (UnsupportedEncodingException e) {
				logger.error("StringCodec", e);
			}
		}
		return new DecResult(ret, ArrayUtils.subarray(bytes, length,
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
		String value = (String) ctx.getEncObject();
		ByteFieldDesc desc = ctx.getFieldDesc();
		NumberCodec numberCodec = ctx.getNumberCodec();

		if (null == desc) {
			throw new RuntimeException("StringCodec: ByteFieldDesc is null");
		}

		String charset = numberCodec.convertCharset(desc.getCharset());

		byte[] bytes = null;

		if (null == value) {
			bytes = new byte[0];
		} else {
			try {
				bytes = value.getBytes(charset);
			} catch (UnsupportedEncodingException e) {
				logger.error("StringCodec", e);
			}
		}

		int fixedLength = desc.getFixedLength();
		if (fixedLength >= 0) {
			while (bytes.length < fixedLength) {
				// fill pending with 0
				bytes = ArrayUtils.add(bytes, (byte) 0);
			}
			while (bytes.length > fixedLength) {
				// fill pending with 0
				bytes = ArrayUtils.remove(bytes, bytes.length - 1);
			}

		}

		return bytes;
	}

}
