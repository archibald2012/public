
package edu.hziee.common.lang;

import java.lang.reflect.Method;

import org.apache.commons.lang.ArrayUtils;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: MethodInvoker.java 14 2012-01-10 11:54:14Z archie $
 */
public class MethodInvoker {
	public MethodInvoker(Object methodOwner, String methodName, Object... args)
			throws Exception {
		if (null == methodOwner) {
			throw new RuntimeException(" methodOwner is null.");
		}

		Method[] methods = null;
		Class<?> itr = methodOwner.getClass();
		while (!itr.equals(Object.class)) {
			methods = (Method[]) ArrayUtils.addAll(itr.getDeclaredMethods(),
					methods);
			itr = itr.getSuperclass();
		}

		Method method = null;
		for (Method methodItr : methods) {
			if (methodItr.getName().equals(methodName)) {
				methodItr.setAccessible(true);
				method = methodItr;
			}
		}

		if (null == method) {
			throw new RuntimeException("method [" + methodOwner.getClass()
					+ "." + methodName + "] !NOT! exist.");
		}

		method.invoke(methodOwner, args);
	}

	public MethodInvoker(Object methodOwner, String methodName, Object arg1,
			Object arg2) throws Exception {
		this(methodOwner, methodName, new Object[] { arg1, arg2 });
	}
}
