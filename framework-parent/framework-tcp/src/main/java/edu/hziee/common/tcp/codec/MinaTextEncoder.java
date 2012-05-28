
package edu.hziee.common.tcp.codec;

import org.apache.commons.lang.ArrayUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.serialization.bytebean.codec.DefaultNumberCodecs;
import edu.hziee.common.serialization.bytebean.codec.NumberCodec;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: MinaTextEncoder.java 14 2012-01-10 11:54:14Z archie $
 */
public class MinaTextEncoder implements ProtocolEncoder {

	private static final Logger logger = LoggerFactory
			.getLogger(MinaTextEncoder.class);

	private static final String ENCODING = "UTF-8";

	private int dumpBytes = 256;
	private boolean isDebugEnabled;
	private NumberCodec numberCodec;

	/* (non-Javadoc)
	 * @see org.apache.mina.filter.codec.ProtocolEncoder#dispose(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void dispose(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.mina.filter.codec.ProtocolEncoder#encode(org.apache.mina.core.session.IoSession, java.lang.Object, org.apache.mina.filter.codec.ProtocolEncoderOutput)
	 */
	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		if (message instanceof String) {
			byte[] bytes = ((String) message).getBytes(ENCODING);
			
			if (null != bytes) {
				bytes = (byte[]) ArrayUtils.addAll(
						getNumberCodec().int2Bytes(bytes.length, 4), bytes);
				out.write(IoBuffer.wrap(bytes));
			}
		} else {
			logger.error("encode: " + message + " is not String.");
		}
	}

	public boolean isDebugEnabled() {
		return isDebugEnabled;
	}

	public void setDebugEnabled(boolean isDebugEnabled) {
		this.isDebugEnabled = isDebugEnabled;
	}

	public int getDumpBytes() {
		return dumpBytes;
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

}
