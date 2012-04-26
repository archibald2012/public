package edu.hziee.common.test.client;

import edu.hziee.common.serialization.protocol.xip.XipResponse;
import edu.hziee.common.serialization.protocol.xip.XipSignal;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: XipCase.java 14 2012-01-10 11:54:14Z archie $
 */
public abstract class XipCase {

  final public String getName() {
    return this.getClass().getName();
  }

  public void checkResp(XipResponse resp) {
  }

  public long waitTime() {
    return 60000L;
  }

  public abstract XipSignal createReq();

}
