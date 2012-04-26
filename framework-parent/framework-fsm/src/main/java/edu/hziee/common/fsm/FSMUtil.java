/**
 * 
 */
package edu.hziee.common.fsm;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ubuntu-admin
 * 
 */
public class FSMUtil {
  private static final Logger logger = LoggerFactory.getLogger(FSMUtil.class);

  public static String getStateNameOfInstance(State<?> state) {
    if (PropertyUtils.isReadable(state, "name")) {
      try {
        return BeanUtils.getSimpleProperty(state, "name");
      } catch (Exception e) {
        logger.error("getStateNameOfInstance:", e);
        return null;
      }
    }

    return null;
  }
}
