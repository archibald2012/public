package edu.hziee.common.metrics.util;

public class MetricsUtil {

	public static String truncate(String value, int size) {
		if (value != null && value.length() > size) {
			return value.substring(0, size);
		}
		return value;
	}

	public static String truncate(String value, int size, String def) {
		if (value != null && value.length() > 0) {
			if (value.length() > size) {
				return value.substring(0, size);
			}
		} else {
			value = def;
		}
		return value;
	}
}
