/*******************************************************************************
 * CopyRight (c) 2005-2011 TAOTAOSOU Co, Ltd. All rights reserved.
 * Filename:    DateConverter.java
 * Creator:     Administrator
 * Create-Date: 2011-5-20 上午10:06:01
 *******************************************************************************/
package edu.hziee.common.test.db.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.springframework.context.i18n.LocaleContextHolder;

import edu.hziee.common.lang.StringUtil;

/**
 * TODO
 * 
 * @author Administrator
 * @version $Id: DateConverter.java 14 2012-01-10 11:54:14Z archie $
 */
public class DateConverter implements Converter {

	private static String resource = "envParams";
	private static String defaultDateTimePattern = null;
	private static String defaultDatePattern = null;

	@Override
	public Object convert(Class type, Object value) {
		if (value == null) {
			return null;
		} else if (type == Timestamp.class) {
			return convertToDate(type, value);
		} else if (type == Date.class) {
			return convertToDate(type, value);
		} else if (type == String.class) {
			return convertToString(type, value);
		}

		throw new ConversionException("Could not convert "
				+ value.getClass().getName() + " to " + type.getName());
	}

	public static synchronized String getDatePattern() {
		Locale locale = LocaleContextHolder.getLocale();
		try {
			defaultDatePattern = ResourceBundle.getBundle(resource, locale)
					.getString("datePattern");
		} catch (MissingResourceException mse) {
			defaultDatePattern = "yyyy-MM-dd";
		}

		return defaultDatePattern;
	}

	public static synchronized String getDateTimePattern() {
		Locale locale = LocaleContextHolder.getLocale();
		try {
			defaultDateTimePattern = ResourceBundle.getBundle(resource, locale)
					.getString("dateTimePattern");
		} catch (MissingResourceException mse) {
			defaultDateTimePattern = "yyyy-MM-dd HH:mm:ss";
		}

		return defaultDateTimePattern;
	}

	protected Object convertToDate(Class<?> type, Object value) {
		if (value instanceof String) {
			try {
				if (StringUtil.isEmpty(value.toString())) {
					return null;
				}
				String pattern = getDatePattern();
				if (value.toString().contains(":")) {
					pattern = getDateTimePattern();
				}
				DateFormat df = new SimpleDateFormat(pattern);
				Date date = df.parse((String) value);
				if (type.equals(Timestamp.class)) {
					return new Timestamp(date.getTime());
				}
				return date;
			} catch (Exception pe) {
				pe.printStackTrace();
				throw new ConversionException("Error converting String to Date");
			}
		}

		throw new ConversionException("Could not convert "
				+ value.getClass().getName() + " to " + type.getName());
	}

	private Object convertToString(Class<?> type, Object value) {

		if (value instanceof Date) {
			DateFormat df = new SimpleDateFormat(getDatePattern());
			if (value instanceof Timestamp) {
				df = new SimpleDateFormat(getDateTimePattern());
			}

			try {
				return df.format(value);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ConversionException("Error converting Date to String");
			}
		} else {
			return value.toString();
		}
	}
}
