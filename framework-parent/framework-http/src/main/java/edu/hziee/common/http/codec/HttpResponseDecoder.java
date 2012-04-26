package edu.hziee.common.http.codec;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.ByteUtil;
import edu.hziee.common.serialization.bytebean.codec.AnyCodec;
import edu.hziee.common.serialization.bytebean.codec.DefaultCodecProvider;
import edu.hziee.common.serialization.bytebean.codec.DefaultNumberCodecs;
import edu.hziee.common.serialization.bytebean.codec.array.LenArrayCodec;
import edu.hziee.common.serialization.bytebean.codec.array.LenListCodec;
import edu.hziee.common.serialization.bytebean.codec.bean.BeanFieldCodec;
import edu.hziee.common.serialization.bytebean.codec.bean.EarlyStopBeanCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.ByteCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.CStyleStringCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.IntCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.LenByteArrayCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.LongCodec;
import edu.hziee.common.serialization.bytebean.codec.primitive.ShortCodec;
import edu.hziee.common.serialization.bytebean.context.DefaultDecContextFactory;
import edu.hziee.common.serialization.bytebean.context.DefaultEncContextFactory;
import edu.hziee.common.serialization.bytebean.field.DefaultField2Desc;
import edu.hziee.common.serialization.protocol.meta.MsgCode2TypeMetainfo;
import edu.hziee.common.serialization.protocol.xip.XipHeader;
import edu.hziee.common.serialization.protocol.xip.XipSignal;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: HttpResponseDecoder.java 4 2012-01-10 11:51:54Z archie $
 */
public class HttpResponseDecoder extends OneToOneDecoder {

  private static final Logger  logger    = LoggerFactory.getLogger(HttpResponseDecoder.class);

  private BeanFieldCodec       byteBeanCodec;
  private MsgCode2TypeMetainfo typeMetaInfo;

  private int                  dumpBytes = 256;
  private boolean              isDebugEnabled;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.jboss.netty.handler.codec.oneone.OneToOneDecoder#decode(org.jboss.netty
   * .channel.ChannelHandlerContext, org.jboss.netty.channel.Channel,
   * java.lang.Object)
   */
  @Override
  protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
    if (logger.isDebugEnabled()) {
      logger.debug("decode {}", msg);
    }

    if (msg instanceof HttpResponse) {
      HttpResponse response = (HttpResponse) msg;
      if (response.getStatus().getCode() != 200) {
        return msg;
      }

      ChannelBuffer content = response.getContent();
      if (!content.readable()) {
        return msg;
      }

      byte[] bytes = content.array();
      if (logger.isDebugEnabled() && isDebugEnabled) {
        logger.debug(ByteUtil.bytesAsHexString(bytes, dumpBytes));
      }
      XipSignal signal = decodeXipSignal(bytes);
      if (logger.isDebugEnabled() && isDebugEnabled) {
        logger.debug("decoded signal:{}", ToStringBuilder.reflectionToString(signal));
      }
      return signal;
    }
    return msg;
  }

  private XipSignal decodeXipSignal(byte[] bytes) throws Exception {

    XipHeader header = (XipHeader) getByteBeanCodec().decode(getByteBeanCodec().getDecContextFactory().createDecContext(bytes, XipHeader.class, null, null))
        .getValue();

    Class<?> type = typeMetaInfo.find(header.getMessageCode());
    if (null == type) {
      throw new RuntimeException("unknow message code:" + header.getMessageCode());
    }

    byte[] bodyBytes = ArrayUtils.subarray(bytes, XipHeader.HEADER_LENGTH, bytes.length);
    XipSignal signal = (XipSignal) getByteBeanCodec().decode(getByteBeanCodec().getDecContextFactory().createDecContext(bodyBytes, type, null, null))
        .getValue();

    if (null != signal) {
      signal.setIdentification(header.getTransactionAsUUID());
    }

    return signal;
  }

  public void setByteBeanCodec(BeanFieldCodec byteBeanCodec) {
    this.byteBeanCodec = byteBeanCodec;
  }

  public BeanFieldCodec getByteBeanCodec() {
    if (byteBeanCodec == null) {
      DefaultCodecProvider codecProvider = new DefaultCodecProvider();

      // 初始化解码器集合
      codecProvider.addCodec(new AnyCodec()).addCodec(new ByteCodec()).addCodec(new ShortCodec()).addCodec(new IntCodec()).addCodec(new LongCodec())
          .addCodec(new CStyleStringCodec()).addCodec(new LenByteArrayCodec()).addCodec(new LenListCodec()).addCodec(new LenArrayCodec());

      // 对象解码器需要指定字段注释读取方法
      EarlyStopBeanCodec byteBeanCodec = new EarlyStopBeanCodec(new DefaultField2Desc());
      codecProvider.addCodec(byteBeanCodec);

      DefaultEncContextFactory encContextFactory = new DefaultEncContextFactory();
      DefaultDecContextFactory decContextFactory = new DefaultDecContextFactory();

      encContextFactory.setCodecProvider(codecProvider);
      encContextFactory.setNumberCodec(DefaultNumberCodecs.getLittleEndianNumberCodec());

      decContextFactory.setCodecProvider(codecProvider);
      decContextFactory.setNumberCodec(DefaultNumberCodecs.getLittleEndianNumberCodec());

      byteBeanCodec.setDecContextFactory(decContextFactory);
      byteBeanCodec.setEncContextFactory(encContextFactory);

      this.byteBeanCodec = byteBeanCodec;
    }
    return byteBeanCodec;
  }

  public void setTypeMetaInfo(MsgCode2TypeMetainfo typeMetaInfo) {
    this.typeMetaInfo = typeMetaInfo;
  }

  public void setDumpBytes(int dumpBytes) {
    this.dumpBytes = dumpBytes;
  }

  public MsgCode2TypeMetainfo getTypeMetaInfo() {
    return typeMetaInfo;
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
