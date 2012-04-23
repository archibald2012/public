/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    ClassUtil.java
 * Creator:     wangqi
 * Create-Date: 2011-6-29 下午01:14:50
 *******************************************************************************/
package edu.hziee.common.lang;

import java.lang.reflect.Method;

import org.apache.commons.lang.ArrayUtils;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: ClassUtil.java 14 2012-01-10 11:54:14Z archie $
 */
public class ClassUtil {

  public static Method[] getAllMethodOf(final Class<?> courseClass) {
    Method[] methods = null;

    Class<?> itr = courseClass;
    while (!itr.equals(Object.class)) {
      methods = (Method[]) ArrayUtils.addAll(itr.getDeclaredMethods(), methods);
      itr = itr.getSuperclass();
    }

    return methods;
  }

  public static String getSimpleName(Class<?> c) {
    return null != c ? c.getSimpleName() : null;
  }
}
