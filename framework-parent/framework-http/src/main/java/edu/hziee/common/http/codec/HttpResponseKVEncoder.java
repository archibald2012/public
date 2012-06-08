/**
 * 
 */
package edu.hziee.common.http.codec;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.http.TransportUtil;
import edu.hziee.common.lang.Transformer;
import edu.hziee.common.serialization.kv.codec.DefaultKVCodec;
import edu.hziee.common.serialization.kv.codec.KVCodec;
import edu.hziee.common.serialization.protocol.xip.XipSignal;

/**
 * @author Administrator
 * 
 */
public class HttpResponseKVEncoder implements Transformer<Object, HttpResponse> {

  private static final Logger logger  = LoggerFactory.getLogger(HttpResponseKVEncoder.class);

  private KVCodec             kvCodec = new DefaultKVCodec();
  private boolean             isDebugEnabled;

  @Override
  public HttpResponse transform(Object signal) {
    DefaultHttpResponse resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

    resp.setStatus(HttpResponseStatus.OK);
    resp.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/x-tar");

    if (signal instanceof XipSignal) {
      String string = encodeXip((XipSignal) signal);
      if (logger.isDebugEnabled()) {
        logger.debug("signal as string:{} \r\n{} ", string);
      }
      if (null != string) {
        resp.setContent(ChannelBuffers.wrappedBuffer(string.getBytes()));
        resp.setHeader(HttpHeaders.Names.CONTENT_LENGTH, string.length());
      }
    }

    HttpRequest req = TransportUtil.getRequestOf(signal);
    if (req != null) {
      String uuid = req.getHeader("uuid");
      if (uuid != null) {
        resp.setHeader("uuid", uuid);
      }

      // 是否需要持久连接
      String keepAlive = req.getHeader(HttpHeaders.Names.CONNECTION);
      if (keepAlive != null) {
        resp.setHeader(HttpHeaders.Names.CONNECTION, keepAlive);
      }
    }

    return resp;
  }

  private String encodeXip(XipSignal signal) {
    String string = getKvCodec().encode(getKvCodec().getEncContextFactory().createEncContext(signal, signal.getClass()));

    return string;
  }

  public KVCodec getKvCodec() {
    return kvCodec;
  }

  public void setKvCodec(KVCodec kvCodec) {
    this.kvCodec = kvCodec;
  }

  public boolean isDebugEnabled() {
    return isDebugEnabled;
  }

  public void setDebugEnabled(boolean isDebugEnabled) {
    this.isDebugEnabled = isDebugEnabled;
  }

}
