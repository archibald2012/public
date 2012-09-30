package edu.hziee.common.metrics.util;

public class CommonUtil {

	public static long getMilliseconds(long time, IntervalUnit timeUnit) {
		long millis = 0;
		switch (timeUnit) {
		case HOUR:
			millis = time * 3600000;
			break;
		case MINUTE:
			millis = time * 60000;
			break;
		case SECOND:
			millis = time * 1000;
			break;
		default:
			millis = time;
		}

		return millis;
	}

}
