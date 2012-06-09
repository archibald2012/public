package edu.hziee.common.xslt2web.sysutil;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public final class ErrorUtil {
	private ErrorUtil() {
	}

	public static synchronized int getID(String path) {
		String fileName = FileUtil.combin(path, "seed.ini");
		int result = 1;
		try {
			FileInputStream stream = new FileInputStream(fileName);
			try {
				byte[] data = new byte[stream.available()];
				stream.read(data);
				try {
					result = Integer.parseInt(new String(data)) + 1;
				} catch (Exception e) {
				}
			} finally {
				stream.close();
			}
		} catch (Exception e) {
		}
		try {
			FileOutputStream stream = new FileOutputStream(fileName);
			try {
				stream.write(Integer.toString(result).getBytes());
				stream.flush();
			} finally {
				stream.close();
			}
		} catch (Exception e) {
		}
		return result;
	}
}
