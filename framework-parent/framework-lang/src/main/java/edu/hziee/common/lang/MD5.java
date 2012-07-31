package edu.hziee.common.lang;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MD5 {

	private static final Logger	logger	= LoggerFactory.getLogger(MD5.class);

	public static byte[] encrypt(byte[] src, byte[] key) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(src);
			return md5.digest(key);
		} catch (NoSuchAlgorithmException e) {
			logger.error("", e);
			throw new RuntimeException(e);
		}
	}
}
