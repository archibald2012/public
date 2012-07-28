package edu.hziee.common.tcp.codec;

import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.ByteUtil;
import edu.hziee.common.lang.DES;
import edu.hziee.common.serialization.bytebean.codec.AnyCodec;
import edu.hziee.common.serialization.bytebean.codec.DefaultCodecProvider;
import edu.hziee.common.serialization.bytebean.codec.DefaultNumberCodecs;
import edu.hziee.common.serialization.bytebean.codec.array.LenArrayCodec;
import edu.hziee.common.serialization.bytebean.codec.array.LenListCodec;
import edu.hziee.common.serialization.bytebean.codec.bean.BeanFieldCodec;
import edu.hziee.common.serialization.bytebean.codec.bean.EarlyStopBeanCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.BooleanCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.ByteCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.CStyleStringCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.DoubleCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.FloatCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.IntCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.LenByteArrayCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.LongCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.ShortCodec;
import edu.hziee.common.serialization.bytebean.context.DefaultDecContextFactory;
import edu.hziee.common.serialization.bytebean.context.DefaultEncContextFactory;
import edu.hziee.common.serialization.bytebean.field.DefaultField2Desc;
import edu.hziee.common.serialization.protocol.annotation.SignalCode;
import edu.hziee.common.serialization.protocol.xip.XipHeader;
import edu.hziee.common.serialization.protocol.xip.XipSignal;
import edu.hziee.common.tcp.TransportUtil;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: MinaXipEncoder.java 61 2012-02-24 10:07:19Z archie $
 */
public class MinaXipEncoder implements ProtocolEncoder {

	private static final Logger	logger		= LoggerFactory.getLogger(MinaXipEncoder.class);

	private int									dumpBytes	= 256;
	private boolean							isDebugEnabled;
	private BeanFieldCodec			byteBeanCodec;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.mina.filter.codec.ProtocolEncoder#encode(org.apache.mina.core .session.IoSession, java.lang.Object,
	 * org.apache.mina.filter.codec.ProtocolEncoderOutput)
	 */
	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		byte[] bytes = null;
		if (message instanceof XipSignal) {
			byte[] key = TransportUtil.getEncryptKeyOfSession(session);
			bytes = encodeXip((XipSignal) message, key);
		} else if (byte[].class.isAssignableFrom(message.getClass())) {
			bytes = (byte[]) message;
		} else {
			throw new RuntimeException("encode: bean " + message + " is not XipSignal.");
		}
		if (null != bytes) {
			if (logger.isTraceEnabled()) {
				logger.trace("bean type {" + message.getClass() + "} and encode size is {" + bytes.length + "}");
			}
			out.write(IoBuffer.wrap(bytes));
		} else {
			logger.error("encode: " + message + " can not generate byte stream.");
		}

	}

	private byte[] encodeXip(XipSignal signal, byte[] key) throws Exception {
		// once
		byte[] bytesBody = getByteBeanCodec().encode(
				getByteBeanCodec().getEncContextFactory().createEncContext(signal, signal.getClass(), null));

		if (key != null) {
			// 对消息体进行DES加密
			bytesBody = DES.encrypt(bytesBody, key);
		}

		SignalCode attr = signal.getClass().getAnnotation(SignalCode.class);
		if (null == attr) {
			throw new RuntimeException("invalid ssip signal, bcs of no messageCode.");
		}

		XipHeader header = createHeader((byte) 1, signal.getIdentification(), attr.messageCode(), bytesBody.length);

		// 更新原语类型
		header.setTypeForClass(signal.getClass());

		byte[] bytes = ArrayUtils.addAll(
				getByteBeanCodec().encode(
						getByteBeanCodec().getEncContextFactory().createEncContext(header, XipHeader.class, null)), bytesBody);

		if (logger.isDebugEnabled() && isDebugEnabled) {
			logger.debug("encode XipSignal:" + signal);
			logger.debug("and XipSignal raw bytes -->");
			logger.debug(ByteUtil.bytesAsHexString(bytes, dumpBytes));
		}

		return bytes;
	}

	private XipHeader createHeader(byte basicVer, UUID id, int messageCode, int messageLen) {

		XipHeader header = new XipHeader();

		header.setTransaction(id);

		int headerSize = getByteBeanCodec().getStaticByteSize(XipHeader.class);

		header.setLength(headerSize + messageLen);
		header.setMessageCode(messageCode);
		header.setBasicVer(basicVer);

		return header;
	}

	public BeanFieldCodec getByteBeanCodec() {
		if (byteBeanCodec == null) {
			DefaultCodecProvider codecProvider = new DefaultCodecProvider();

			// 初始化解码器集合
			codecProvider.addCodec(new AnyCodec()).addCodec(new ByteCodec()).addCodec(new BooleanCodec())
					.addCodec(new ShortCodec()).addCodec(new IntCodec()).addCodec(new LongCodec()).addCodec(new FloatCodec())
					.addCodec(new DoubleCodec()).addCodec(new CStyleStringCodec()).addCodec(new LenByteArrayCodec())
					.addCodec(new LenListCodec()).addCodec(new LenArrayCodec());

			// 对象解码器需要指定字段注释读取方泄1�7
			EarlyStopBeanCodec byteBeanCodec = new EarlyStopBeanCodec(new DefaultField2Desc());
			codecProvider.addCodec(byteBeanCodec);

			DefaultEncContextFactory encContextFactory = new DefaultEncContextFactory();
			DefaultDecContextFactory decContextFactory = new DefaultDecContextFactory();

			encContextFactory.setCodecProvider(codecProvider);
			encContextFactory.setNumberCodec(DefaultNumberCodecs.getBigEndianNumberCodec());

			decContextFactory.setCodecProvider(codecProvider);
			decContextFactory.setNumberCodec(DefaultNumberCodecs.getBigEndianNumberCodec());

			byteBeanCodec.setDecContextFactory(decContextFactory);
			byteBeanCodec.setEncContextFactory(encContextFactory);

			this.byteBeanCodec = byteBeanCodec;
		}
		return byteBeanCodec;
	}

	public void setByteBeanCodec(BeanFieldCodec byteBeanCodec) {
		this.byteBeanCodec = byteBeanCodec;
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

	/**
	 * @param dumpBytes
	 *          the dumpBytes to set
	 */
	public void setDumpBytes(int dumpBytes) {
		this.dumpBytes = dumpBytes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.mina.filter.codec.ProtocolEncoder#dispose(org.apache.mina.core .session.IoSession)
	 */
	@Override
	public void dispose(IoSession arg0) throws Exception {
	}
}
