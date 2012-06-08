
package edu.hziee.common.lang;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 
 * @author wangqi
 * @version $Id: FileUtil.java 14 2012-01-10 11:54:14Z archie $
 */
public class FileUtil {

	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

	public static File createFile(String filename) throws Exception {
		File file = newFile(filename);
		if (!file.canWrite()) {
			String dirName = file.getPath();
			int i = dirName.lastIndexOf(File.separator);
			if (i > -1) {
				dirName = dirName.substring(0, i);
				File dir = newFile(dirName);
				dir.mkdirs();
			}
			file.createNewFile();
		}
		return file;
	}

	public static int getFileSize(String fileName) {
		File file = new File(fileName);
		FileInputStream fis = null;
		try {
			if (file.exists()) {
				fis = new FileInputStream(file);
				return fis.available();
			}
		} catch (FileNotFoundException e) {
			logger.error("", e);
		} catch (IOException e) {
			logger.error("", e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error("failed to close file", e);
				}
			}
		}
		return 0;
	}

	public static File newFile(String pathName) throws Exception {
		try {
			return new File(pathName).getCanonicalFile();
		} catch (IOException e) {
			logger.error("", e);
			throw new Exception("创建文件失败", e);
		}
	}

	public static String getClassFilePath(Class<?> clazz) {
		java.net.URL url = clazz.getProtectionDomain().getCodeSource()
				.getLocation();
		String filePath = null;
		try {
			filePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
		} catch (UnsupportedEncodingException ignore) {
		}
		File file = new java.io.File(filePath);
		return file.getAbsolutePath();
	}

	public static void writeByteArraysToFile(File file, List<byte[]> bytes) {
		BufferedOutputStream bufferedOut = null;
		try {
			FileOutputStream out = openOutputStream(file, true);
			bufferedOut = new BufferedOutputStream(out);
			for (byte[] s : bytes) {
				bufferedOut.write(s);
			}
			bufferedOut.flush();
		} catch (Exception e) {
			logger.error(">>>> Excute write Exception: ", e);
		} finally {
			try {
				if (bufferedOut != null)
					bufferedOut.close();
			} catch (IOException e) {
				logger.error(">>>> Clost BufferedWriter Exception:", e);
			}
		}
	}

	public static FileOutputStream openOutputStream(File file, boolean append)
			throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file
						+ "' exists but is a directory");
			}
			if (file.canWrite() == false) {
				throw new IOException("File '" + file
						+ "' cannot be written to");
			}
		} else {
			File parent = file.getParentFile();
			if (parent != null && parent.exists() == false) {
				if (parent.mkdirs() == false) {
					throw new IOException("File '" + file
							+ "' could not be created");
				}
			}
		}
		return new FileOutputStream(file, true);
	}

}
