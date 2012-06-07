package edu.hziee.common.dispatcher.receiver;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hziee.common.dispatcher.course.BizMethod;
import edu.hziee.common.dispatcher.course.BusinessCourse;
import edu.hziee.common.lang.ClassUtil;
import edu.hziee.common.lang.SimpleCache;
import edu.hziee.common.lang.statistic.TransactionStatisticer;
import edu.hziee.common.lang.transport.Receiver;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: SimpleDispatcher.java 14 2012-01-10 11:54:14Z archie $
 */
public class SimpleDispatcher implements Receiver {

	private static final Logger						logger			= LoggerFactory.getLogger(SimpleDispatcher.class);

	private Map<Class<?>, BusinessCourse>	courseTable	= new HashMap<Class<?>, BusinessCourse>();

	private TransactionStatisticer				statisticer	= new TransactionStatisticer();

	private static final class Key {

		private Class<?>	courseClass;
		private Class<?>	beanClass;

		public Key(Class<?> courseClass, Class<?> beanClass) {
			this.courseClass = courseClass;
			this.beanClass = beanClass;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((beanClass == null) ? 0 : beanClass.hashCode());
			result = prime * result + ((courseClass == null) ? 0 : courseClass.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Key))
				return false;
			final Key other = (Key) obj;
			if (beanClass == null) {
				if (other.beanClass != null)
					return false;
			} else if (!beanClass.equals(other.beanClass))
				return false;
			if (courseClass == null) {
				if (other.courseClass != null)
					return false;
			} else if (!courseClass.equals(other.courseClass))
				return false;
			return true;
		}
	}

	private static final Method				EMPTY_METHOD;

	static {
		Method tmp = null;
		try {
			tmp = Key.class.getMethod("hashCode");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		EMPTY_METHOD = tmp;
	}

	private SimpleCache<Key, Method>	bizMethodCache	= new SimpleCache<Key, Method>();

	private Method getBizMethod(final Class<?> courseClass, final Class<?> beanClass) {

		Method ret = bizMethodCache.get(new Key(courseClass, beanClass), new Callable<Method>() {

			public Method call() throws Exception {
				Method[] methods = ClassUtil.getAllMethodOf(courseClass);

				for (Method method : methods) {
					BizMethod biz = method.getAnnotation(BizMethod.class);

					if (null != biz) {
						Class<?>[] params = method.getParameterTypes();
						if (params.length < 1) {
							if (logger.isWarnEnabled()) {
								logger.warn("Method [" + method.getName() + "] found but only [" + params.length
										+ "] parameters found, need to be 1.");
							}
							continue;
						}
						if (params[0].isAssignableFrom(beanClass)) {
							return method;
						}
					}
				}

				return EMPTY_METHOD;
			}
		});
		return (ret == EMPTY_METHOD) ? null : ret;
	}

	public void messageReceived(final Object input) {

		final Object message = input;

		BusinessCourse course = getCourse(message.getClass());
		if (course == null) {
			if (logger.isErrorEnabled()) {
				logger.error("No course class found for [" + message.getClass().getName() + "]. Process stopped.");
			}
			return;
		}
		try {
			if (statisticer != null) {
				statisticer.incHandledTransactionStart();
			}
			invokeBizMethod(course, message);
			if (statisticer != null) {
				statisticer.incHandledTransactionEnd();
			}
		} catch (Exception e) {
			logger.error("biz error.", e);
		}

	}

	private void invokeBizMethod(BusinessCourse course, Object msg) {
		Method bizMethod = getBizMethod(course.getClass(), msg.getClass());
		if (null != bizMethod) {
			try {
				bizMethod.invoke(course, msg);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("Invoke biz method [" + bizMethod.getName() + "] failed. ", e);
				}
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("No biz method found for message [" + msg.getClass().getName() + "]. No process execute.");
			}
		}
	}

	private BusinessCourse getCourse(Class<?> clazz) {
		return courseTable.get(clazz);
	}

	public void setCourses(Collection<BusinessCourse> courses) {
		for (BusinessCourse course : courses) {
			Method[] methods = ClassUtil.getAllMethodOf(course.getClass());
			for (Method method : methods) {
				BizMethod biz = method.getAnnotation(BizMethod.class);
				if (null != biz) {
					Class<?>[] params = method.getParameterTypes();
					if (params.length < 1) {
						continue;
					}
					courseTable.put(params[0], course);
				}
			}
		}
	}

	public void setStatisticer(TransactionStatisticer statisticer) {
		this.statisticer = statisticer;
	}

}
