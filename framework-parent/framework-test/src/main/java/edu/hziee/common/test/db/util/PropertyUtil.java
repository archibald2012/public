/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    PropertyUtil.java
 * Creator:     Administrator
 * Create-Date: 2011-5-20 上午10:14:41
 *******************************************************************************/
package edu.hziee.common.test.db.util;

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import edu.hziee.common.lang.StringUtil;

/**
 * TODO
 * 
 * @author Administrator
 * @version $Id: PropertyUtil.java 14 2012-01-10 11:54:14Z archie $
 */
public class PropertyUtil {

	/**
	 * 把properties文件key-value信息转换为Map,如果key中含有下换线转换为驼峰风格
	 * 
	 * @param resourceName
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String> loadProperties(String resourceName) {

		Map<String, String> paramsMap = Collections
				.synchronizedMap(new CamelCasingHashMap());
		try {

			Properties properties = loadAllProperties(resourceName,
					PropertyUtil.class.getClassLoader());
			for (Enumeration keys = properties.keys(); keys.hasMoreElements();) {
				String key = (String) keys.nextElement();
				paramsMap.put(key, properties.getProperty(key).trim());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return paramsMap;
	}

	public static Properties loadAllProperties(String resourceName,
			ClassLoader classLoader) throws IOException {
		Assert.notNull(resourceName, "Resource name must not be null");
		ClassLoader clToUse = classLoader;
		if (clToUse == null) {
			clToUse = ClassUtils.getDefaultClassLoader();
		}
		Properties properties = new Properties();
		Enumeration urls = clToUse.getResources(resourceName);
		while (urls.hasMoreElements()) {
			URL url = (URL) urls.nextElement();
			InputStream is = null;
			try {
				URLConnection con = url.openConnection();
				con.setUseCaches(false);
				is = con.getInputStream();
				properties.load(is);
			} finally {
				if (is != null) {
					is.close();
				}
			}
		}
		return properties;
	}

	private static class CamelCasingHashMap extends HashMap {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public boolean containsKey(Object key) {
			return super.containsKey(StringUtil.toCamelCasing(key.toString()));
		}

		public Object get(Object key) {
			Object v = super.get(StringUtil.toCamelCasing(key.toString()));
			if (v == null) {
				String error = format(
						"Get property value failed.the value of '%s' is null.",
						key);
				throw new IllegalStateException(error);
			}
			return v;
		}

		public Object put(Object key, Object value) {
			return super.put(StringUtil.toCamelCasing(key.toString()), value);
		}

		public Object remove(Object key) {
			return super.remove(StringUtil.toCamelCasing(key.toString()));
		}

	}
}
