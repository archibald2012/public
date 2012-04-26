/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    BaseXDataSetTestCase.java
 * Creator:     Administrator
 * Create-Date: 2011-5-20 上午10:05:05
 *******************************************************************************/
package edu.hziee.common.test.db;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.util.ClassUtils;

import edu.hziee.common.test.db.util.DateConverter;
import edu.hziee.common.test.db.util.PropertyUtil;

/**
 * 
 * 不支持测试类的数据回滚，支持多数据源
 * 
 * @author Administrator
 * @version $Id: BaseXDataSetTestCase.java 14 2012-01-10 11:54:14Z archie $
 */
@TestExecutionListeners({ XDataSetTestExecutionListener.class })
public class BaseXDataSetTestCase extends AbstractJUnit4SpringContextTests {
	static {
		ConvertUtils.register(new DateConverter(), Timestamp.class);
		ConvertUtils.register(new DateConverter(), Date.class);
		ConvertUtils.register(new DateConverter(), String.class);
	}

	/**
	 * 默认按by_name装配,可根据需要修改
	 */
	protected int autowireMode = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

	protected final Logger logger = LoggerFactory
			.getLogger(BaseXDataSetTestCase.class);

	protected Map<String, String> properties;

	public BaseXDataSetTestCase() {
		try {
			String propertiesPath = this.getClass().getName().replace('.', '/')
					+ ".properties";
			properties = PropertyUtil.loadProperties(propertiesPath);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	protected Map<String, String> propertiesToMap(String propertiesName) {
		String propertiesPath = this.getClass().getPackage().getName()
				.replace('.', '/')
				+ "/" + propertiesName + ".properties";
		return PropertyUtil.loadProperties(propertiesPath);
	}

	/**
	 * 将properties值拷贝到bean
	 * 
	 * @param bean
	 * @return
	 */
	protected Object populate(Object bean) {
		try {
			BeanUtils.populate(bean, properties);
		} catch (Exception e) {
			throw new FatalBeanException("Could not copy properties to target",
					e);
		}
		return bean;
	}

	/**
	 * 将properties值拷贝到bean,支持JavaBean一级嵌套
	 * 
	 * @param type
	 * @return
	 */
	protected <T> T populate(Class<T> type) {
		T bean = null;
		try {
			bean = type.newInstance();
			PropertyDescriptor[] props = BeanUtilsBean.getInstance()
					.getPropertyUtils().getPropertyDescriptors(bean);
			for (int i = 0; i < props.length; i++) {
				Class<?> subType = props[i].getPropertyType();
				if (null != subType && !subType.getName().startsWith("java.")
						&& !subType.isPrimitive() && !subType.isArray()) {
					props[i].getWriteMethod().invoke(bean,
							subType.newInstance());
				}
			}
			populate(bean);
		} catch (Exception e) {
			throw new FatalBeanException("Could not copy properties to target",
					e);
		}
		return bean;
	}

	/**
	 * 将map值拷贝到bean,支持JavaBean一级嵌套
	 * 
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T populate(Class<T> type, Map map) {
		T bean = null;
		try {
			bean = type.newInstance();
			PropertyDescriptor[] props = BeanUtilsBean.getInstance()
					.getPropertyUtils().getPropertyDescriptors(bean);
			for (int i = 0; i < props.length; i++) {
				Class<?> subType = props[i].getPropertyType();
				if (!subType.getName().startsWith("java.")
						&& !subType.isPrimitive()) {
					props[i].getWriteMethod().invoke(bean,
							subType.newInstance());
				}
			}
			BeanUtils.populate(bean, map);
		} catch (Exception e) {
			throw new FatalBeanException("Could not copy properties to target",
					e);
		}
		return bean;
	}

	/**
	 * 从classpath加载文件
	 * 
	 * @param location
	 * @return
	 */
	protected File getResourceAsFile(String location) {
		if (!location.contains("classpath:/")) {
			location = "classpath:/"
					+ ClassUtils.getPackageName(this.getClass()).replace('.',
							'/') + "/" + location;
		}
		try {
			return new DefaultResourceLoader().getResource(location).getFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
