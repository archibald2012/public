package edu.hziee.common.test.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: AbstractClientTestCase.java 14 2012-01-10 11:54:14Z archie $
 */
public class AbstractClientTestCase extends AbstractJUnit4SpringContextTests {

  private static final Logger   logger = LoggerFactory.getLogger(AbstractClientTestCase.class);

  protected Client              client = null;

  protected Map<String, String> properties;

  public AbstractClientTestCase() {
    try {
      String propertiesPath = this.getClass().getName().replace('.', '/') + ".properties";

    } catch (Exception e) {
      logger.error("", e);
    }
  }

  public Client getClient() {
    return client;
  }

  public void setClient(Client client) {
    this.client = client;
  }

}
