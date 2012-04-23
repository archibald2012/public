/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    MetainfoUtils.java
 * Creator:     wangqi
 * Create-Date: 2011-4-28 上午11:04:32
 *******************************************************************************/
package edu.hziee.common.serialization.protocol.meta;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.lang.PackageUtil;
import edu.hziee.common.serialization.protocol.annotation.SignalCode;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: MetainfoUtils.java 14 2012-01-10 11:54:14Z archie $
 */
public class MetainfoUtils {

	private static final Logger logger = LoggerFactory.getLogger(MetainfoUtils.class);

	private static MetainfoUtils util = new MetainfoUtils();

	public static MetainfoUtils getUtil() {
		return util;
	}

	private MetainfoUtils() {
	}

	static public DefaultMsgCode2TypeMetainfo createTypeMetainfo(
			Collection<String> packages) {
		DefaultMsgCode2TypeMetainfo typeMetainfo = new DefaultMsgCode2TypeMetainfo();

		if (null != packages) {
			for (String pkgName : packages) {
				try {
					String[] clsNames = PackageUtil.findClassesInPackage(
							pkgName, null, null);
					for (String clsName : clsNames) {
						try {
							ClassLoader cl = Thread.currentThread()
									.getContextClassLoader();
							if (logger.isDebugEnabled()) {
								logger.debug("using ClassLoader {" + cl
										+ "} to load Class {" + clsName + "}");
							}
							Class<?> cls = cl.loadClass(clsName);
							SignalCode attr = cls
									.getAnnotation(SignalCode.class);
							if (null != attr) {
								int value = attr.messageCode();
								typeMetainfo.add(value, cls);
								logger.info("metainfo: add " + value + ":=>"
										+ cls);
							}
						} catch (ClassNotFoundException e) {
							logger.error("createTypeMetainfo: ", e);
						}
					}
				} catch (IOException e) {
					logger.error("createTypeMetainfo: ", e);
				}
			}
		}

		return typeMetainfo;
	}
}
