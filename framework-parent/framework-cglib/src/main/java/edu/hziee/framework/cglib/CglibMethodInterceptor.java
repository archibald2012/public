/**
 * 
 */
package edu.hziee.framework.cglib;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.metrics.MetricsTimer;
import edu.hziee.framework.cglib.annotation.ProxyMetrics;

/**
 * @author Administrator
 * 
 */
public class CglibMethodInterceptor implements MethodInterceptor {

	private static final Logger	logger	= LoggerFactory.getLogger(CglibMethodInterceptor.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.cglib.proxy.MethodInterceptor#intercept(java.lang.Object, java.lang.reflect.Method, java.lang.Object[],
	 * net.sf.cglib.proxy.MethodProxy)
	 */
	@Override
	public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		ProxyMetrics proxyMetrics = method.getAnnotation(ProxyMetrics.class);
		if (proxyMetrics == null) {
			return proxy.invokeSuper(object, args);
		} else {

			Object result = null;
			MetricsTimer metricsTimer = null;
			try {
				Class<?> clazz = object.getClass().getSuperclass();
				String componentName = proxyMetrics.component();
				if (componentName.length() == 0) {
					componentName = clazz.getSimpleName();
				}
				String functionName = proxyMetrics.function();
				if (functionName.length() == 0) {
					functionName = method.getName();
				}

			} catch (RuntimeException e) {
				logger.warn("Failed to start interceptor with error " + e.getMessage(), e);
			}

			Throwable exception = null;
			try {
				result = proxy.invokeSuper(object, args);
			} catch (Throwable e) {
				exception = e;
				if (logger.isDebugEnabled()) {
					logger.debug("Found exception from method " + method + " with error " + e.getMessage(), e);
				}
				throw e;
			} finally {
				try {
					if (metricsTimer != null) {
						// stop metrics timer

					}
				} catch (RuntimeException e) {
					logger.warn("Failed to stop interceptor with error " + e.getMessage(), e);
				}
			}
			return result;
		}
	}

}
