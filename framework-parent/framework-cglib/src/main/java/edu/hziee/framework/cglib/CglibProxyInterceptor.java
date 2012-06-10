/**
 * 
 */
package edu.hziee.framework.cglib;

import java.util.HashSet;
import java.util.Set;

import net.sf.cglib.proxy.Enhancer;

/**
 * @author Administrator
 * 
 */
public class CglibProxyInterceptor {

	@SuppressWarnings("unchecked")
	public <T> T proxyObject(Class<?> T) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(T);
		enhancer.setInterfaces(getInterfaces(T).toArray(new Class<?>[0]));
		enhancer.setCallback(new CglibMethodInterceptor());
		return (T) enhancer.create();
	}

	private Set<Class<?>> getInterfaces(Class<?> clazz) {
		Set<Class<?>> interfaceList = new HashSet<Class<?>>();

		if (clazz.isInterface()) {
			interfaceList.add(clazz);
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			interfaceList.addAll(getInterfaces(superClass));
		}
		Class<?>[] superInterfaces = clazz.getInterfaces();
		for (int i = 0; i < superInterfaces.length; i++) {
			Class<?> superInterface = superInterfaces[i];
			interfaceList.addAll(getInterfaces(superInterface));
		}
		return interfaceList;
	}
}
