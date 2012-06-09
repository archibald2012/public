package edu.hziee.common.test.client;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.transport.SenderSync;
import edu.hziee.common.serialization.protocol.xip.XipNotify;
import edu.hziee.common.serialization.protocol.xip.XipRequest;
import edu.hziee.common.serialization.protocol.xip.XipResponse;
import edu.hziee.common.serialization.protocol.xip.XipSignal;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: Client.java 64 2012-02-25 01:15:38Z archie $
 */
public class Client {
  private static final Logger logger = LoggerFactory.getLogger(Client.class);

  private SenderSync          connector;

  public void doXipCase(XipCase[] xipCases) {
    if (xipCases == null || xipCases.length == 0) {
      logger.info("xipCases is null, process stoped.");
      return;
    }
    for (XipCase xipCase : xipCases) {
      doXipCase(xipCase);
    }
  }

  public void doXipCase(final XipCase xipCase) {
    if (xipCase == null) {
      logger.info("xipCase is null, process stoped.");
      return;
    }

    XipSignal signal = xipCase.createReq();

    if (signal instanceof XipRequest) {

      XipResponse resp = (XipResponse) connector.sendAndWait(signal, xipCase.waitTime(), TimeUnit.MILLISECONDS);

      xipCase.checkResp(resp);

      logger.info(xipCase.getName() + " pass.");

    } else if (signal instanceof XipNotify) {
      connector.sendAndWait(signal);
    }
  }
  public void setConnector(SenderSync connector) {
    this.connector = connector;
  }

}
