package edu.hziee.common.tcp.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.ByteUtil;
import edu.hziee.common.serialization.bytebean.codec.DefaultNumberCodecs;
import edu.hziee.common.serialization.bytebean.codec.NumberCodec;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: MinaTextDecoder.java 14 2012-01-10 11:54:14Z archie $
 */
public class MinaTextDecoder extends CumulativeProtocolDecoder {

	private static final Logger logger = LoggerFactory
			.getLogger(MinaTextDecoder.class);

	private static final String ENCODING = "UTF-8";

	private int dumpBytes = 256;
	private boolean isDebugEnabled;
	private NumberCodec numberCodec;

	private final AttributeKey BYTE_LENGTH = new AttributeKey(getClass(),
			"byteLength");

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {

		Integer byteLength = (Integer) session.getAttribute(BYTE_LENGTH);

		if (byteLength == null) {
			if (in.remaining() < 4) {
				return false;
			} else {
				byte[] lengthBytes = new byte[4];
				in.get(lengthBytes);

				if (logger.isDebugEnabled() && isDebugEnabled) {
					logger.debug("length raw bytes -->");
					logger.debug(ByteUtil.bytesAsHexString(lengthBytes,
							dumpBytes));
				}

				byteLength = getNumberCodec().bytes2Int(lengthBytes, 4);

				if (logger.isDebugEnabled() && isDebugEnabled) {
					logger.debug("length-->" + byteLength);
				}

				session.setAttribute(BYTE_LENGTH, byteLength);
			}
		}

		if (in.remaining() < byteLength) {
			return false;
		} else {
			session.removeAttribute(BYTE_LENGTH);

			byte[] bytes = new byte[byteLength];
			in.get(bytes);

			if (logger.isDebugEnabled() && isDebugEnabled) {
				logger.debug("raw bytes -->");
				logger.debug(ByteUtil.bytesAsHexString(bytes, dumpBytes));
			}

			String text = new String(bytes, ENCODING);

			if (logger.isDebugEnabled() && isDebugEnabled) {
				logger.debug("signal-->" + text);
			}

			out.write(text);

			return true;
		}

	}

	public NumberCodec getNumberCodec() {
		if (numberCodec == null) {
			numberCodec = DefaultNumberCodecs.getBigEndianNumberCodec();
		}
		return numberCodec;
	}

	public void setNumberCodec(NumberCodec numberCodec) {
		this.numberCodec = numberCodec;
	}

	/**
	 * @param dumpBytes
	 *            the dumpBytes to set
	 */
	public void setDumpBytes(int dumpBytes) {
		this.dumpBytes = dumpBytes;
	}

	public int getDumpBytes() {
		return dumpBytes;
	}

	public boolean isDebugEnabled() {
		return isDebugEnabled;
	}

	public void setDebugEnabled(boolean isDebugEnabled) {
		this.isDebugEnabled = isDebugEnabled;
	}

}
