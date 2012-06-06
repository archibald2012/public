
package edu.hziee.common.tcp.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.ByteUtil;
import edu.hziee.common.lang.DESUtil;
import edu.hziee.common.lang.Identifiable;
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
import edu.hziee.common.serialization.protocol.meta.MsgCode2TypeMetainfo;
import edu.hziee.common.serialization.protocol.xip.XipHeader;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: MinaXipDecoder.java 61 2012-02-24 10:07:19Z archie $
 */
public class MinaXipDecoder extends CumulativeProtocolDecoder {

  private static final Logger  logger           = LoggerFactory.getLogger(MinaXipDecoder.class);

  private final AttributeKey   HEADER           = new AttributeKey(getClass(), "XipHeader");

  private BeanFieldCodec       byteBeanCodec;
  private MsgCode2TypeMetainfo typeMetaInfo;

  private int                  maxMessageLength = -1;

  private int                  dumpBytes        = 256;
  private boolean              isDebugEnabled;
  private byte[]               encryptKey;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.apache.mina.filter.codec.CumulativeProtocolDecoder#doDecode(org.apache
   * .mina.core.session.IoSession, org.apache.mina.core.buffer.IoBuffer,
   * org.apache.mina.filter.codec.ProtocolDecoderOutput)
   */
  @Override
  protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {

    int headerSize = getByteBeanCodec().getStaticByteSize(XipHeader.class);

    XipHeader header = (XipHeader) session.getAttribute(HEADER);
    if (null == header) {

      if (in.remaining() < headerSize) {
        return false;
      } else {
        if (logger.isDebugEnabled() && isDebugEnabled) {
          logger.debug("parse header... try parse...");
        }
        byte[] headerBytes = new byte[headerSize];
        in.get(headerBytes);

        if (logger.isDebugEnabled() && isDebugEnabled) {
          logger.debug("header raw bytes -->");
          logger.debug(ByteUtil.bytesAsHexString(headerBytes, dumpBytes));
        }

        header = (XipHeader) getByteBeanCodec().decode(getByteBeanCodec().getDecContextFactory().createDecContext(headerBytes, XipHeader.class, null, null))
            .getValue();
        if (logger.isDebugEnabled() && isDebugEnabled) {
          logger.debug("header-->" + header);
        }

        if (maxMessageLength > 0) {
          if (header.getLength() > maxMessageLength) {
            logger.error("header.length (" + header.getLength() + ") exceed maxMessageLength[" + maxMessageLength
                + "], so drop this connection.\r\ndump bytes received:\r\n" + ByteUtil.bytesAsHexString(headerBytes, dumpBytes));
            session.close(true);
            return false;
          }
        }

        // Update the session attribute.
        setSessionXipHeader(session, header);
      }
    }

    int bodySize = header.getLength() - headerSize;
    if (in.remaining() < bodySize) {
      return false;
    } else {
      // 为下一次在同一session上进行xip接受初始化环境
      removeSessionXipHeader(session);

      byte[] bytes = new byte[bodySize];
      in.get(bytes);

      if (logger.isDebugEnabled() && isDebugEnabled) {
        logger.debug("body raw bytes -->");
        logger.debug(ByteUtil.bytesAsHexString(bytes, dumpBytes));
      }

      Identifiable identifyable = null;
      if (1 == header.getBasicVer()) {
        Class<?> type = typeMetaInfo.find(header.getMessageCode());
        if (null == type) {
          throw new RuntimeException("unknow message code:" + header.getMessageCode());
        }
        // 对消息体进行DES解密
        if (getEncryptKey() != null) {
          bytes = DESUtil.decrypt(bytes, getEncryptKey());
        }
        identifyable = (Identifiable) getByteBeanCodec().decode(getByteBeanCodec().getDecContextFactory().createDecContext(bytes, type, null, null)).getValue();
      } else {
        logger.error("invalid basic ver, while header is {" + header + "}");
        logger.error("raw body bytes is {" + ByteUtil.bytesAsHexString(bytes, bodySize) + "}");
        throw new RuntimeException("invalid basic ver {" + header.getBasicVer() + "}");
      }

      identifyable.setIdentification(header.getTransactionAsUUID());

      if (logger.isDebugEnabled() && isDebugEnabled) {
        logger.debug("signal-->" + identifyable);
      }

      out.write(identifyable);

      return true;
    }
  }
  @Override
  public void dispose(IoSession session) throws Exception {
    super.dispose(session);

    // our dispose
    removeSessionXipHeader(session);
  }

  private void setSessionXipHeader(IoSession session, XipHeader header) {
    session.setAttribute(HEADER, header);
  }

  private void removeSessionXipHeader(IoSession session) {
    session.removeAttribute(HEADER);
  }

  /**
   * @param dumpBytes
   *          the dumpBytes to set
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

  public BeanFieldCodec getByteBeanCodec() {
    if (byteBeanCodec == null) {
      DefaultCodecProvider codecProvider = new DefaultCodecProvider();

      // 初始化解码器集合
      codecProvider.addCodec(new AnyCodec()).addCodec(new ByteCodec()).addCodec(new BooleanCodec()).addCodec(new ShortCodec()).addCodec(new IntCodec())
          .addCodec(new LongCodec()).addCodec(new FloatCodec()).addCodec(new DoubleCodec()).addCodec(new CStyleStringCodec()).addCodec(new LenByteArrayCodec())
          .addCodec(new LenListCodec()).addCodec(new LenArrayCodec());

      // 对象解码器需要指定字段注释读取方法
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

  public byte[] getEncryptKey() {
    return encryptKey;
  }

  public void setEncryptKey(String encryptKey) {
    this.encryptKey = encryptKey.getBytes();
  }

  public void setEncryptKey(byte[] encryptKey) {
    this.encryptKey = encryptKey;
  }
  
  public void setByteBeanCodec(BeanFieldCodec byteBeanCodec) {
    this.byteBeanCodec = byteBeanCodec;
  }

  public MsgCode2TypeMetainfo getTypeMetaInfo() {
    return typeMetaInfo;
  }

  public void setTypeMetaInfo(MsgCode2TypeMetainfo typeMetaInfo) {
    this.typeMetaInfo = typeMetaInfo;
  }
  /**
   * @return the maxMessageLength
   */
  public int getMaxMessageLength() {
    return maxMessageLength;
  }

  /**
   * @param maxMessageLength
   *          the maxMessageLength to set
   */
  public void setMaxMessageLength(int maxMessageLength) {
    this.maxMessageLength = maxMessageLength;
  }
}
