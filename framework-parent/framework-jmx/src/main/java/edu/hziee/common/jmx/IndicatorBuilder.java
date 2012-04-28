/**
 * 
 */
package edu.hziee.common.jmx;

import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.jmx.annotation.JmxIndicator;

public class IndicatorBuilder {

	private static final Logger	logger	= LoggerFactory.getLogger(IndicatorBuilder.class);

	private String				prefix	= null;

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix
	 *            the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	private final InvocationHandler	handler	= new InvocationHandler() {

												public Object invoke(Object proxy, Method method, Object[] args)
														throws Throwable {
													JmxIndicator target = method.getAnnotation(JmxIndicator.class);

													if (null != target) {
														String objName = target.objectName();
														String operationName = target.operationName();

														if (null != objName && null != operationName) {
															if (null != prefix && objName.startsWith("prefix:")) {
																objName = prefix + objName.substring(7);
															}

															MBeanServer server = ManagementFactory
																	.getPlatformMBeanServer();

															if (null != server) {
																try {
																	return server.invoke(new ObjectName(objName),
																			operationName, null, null);
																} catch (Exception e) {
																	if (logger.isWarnEnabled()) {
																		logger.warn("invoke {}.{} error : " + e,
																				objName, operationName);
																	}
																}
															}
														}
													}

													Object retVal = null;

													Class<?> retType = method.getReturnType();
													if (null != retType) {
														if (retType.equals(int.class) || retType.equals(Integer.class)) {
															retVal = new Integer(0);
														} else if (retType.equals(byte.class)
																|| retType.equals(Byte.class)) {
															retVal = new Byte((byte) 0);
														} else if (retType.equals(short.class)
																|| retType.equals(Short.class)) {
															retVal = new Short((short) 0);
														} else if (retType.equals(long.class)
																|| retType.equals(Long.class)) {
															retVal = new Long((short) 0);
														}
													}

													return retVal;
												}
											};

	@SuppressWarnings("unchecked")
	public <T> T buildIndicator(Class<?> intf) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		return (T) Proxy.newProxyInstance(cl, new Class<?>[] { intf }, handler);
	}

}
