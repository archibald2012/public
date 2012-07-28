/**
 * 
 */
package edu.hziee.common.lang;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Archibald.Wang
 */
public class DES {

	private static final Logger	logger		= LoggerFactory.getLogger(DES.class);

	private final static String	ALGORITHM	= "DES/ECB/NoPadding";

	public static byte[] genKey() throws Exception {
		KeyGenerator kg = KeyGenerator.getInstance("DES"); // 实例化密钥生成器
		kg.init(56); // 初始化密钥生成器
		SecretKey secretKey = kg.generateKey(); // 生成密钥
		return secretKey.getEncoded(); // 获取二进制密钥编码形式
	}

	public static byte[] genKeyByDiffieHellman(PrivateKey privateKey, PublicKey publicKey) throws Exception {
		// Prepare to generate the secret key with the private key and public key of the other party
		KeyAgreement ka = KeyAgreement.getInstance("DH");
		ka.init(privateKey);
		ka.doPhase(publicKey, true);

		// Generate the secret key
		SecretKey secretKey = ka.generateSecret("DES");

		// Use the secret key to encrypt/decrypt data;
		return secretKey.getEncoded();// 获取二进制密钥编码形式
	}

	/**
	 * 加密
	 * 
	 * @param src
	 *          长度为8的倍数
	 * @param key
	 *          长度为8的倍数
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] src, byte[] key) throws Exception {

		src = padding(src, (byte) 0x0);

		// 创建一个密匙工厂，然后用它把DESKeySpec转换成 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

		// 从原始密匙数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		SecretKey securekey = keyFactory.generateSecret(dks);

		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance(ALGORITHM);

		// 用密匙初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey);

		// 获取数据并加密正式执行加密操作
		return cipher.doFinal(src);

	}

	/**
	 * 解密
	 * 
	 * @param src
	 *          长度为8的倍数
	 * @param key
	 *          长度为8的倍数
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] src, byte[] key) throws Exception {

		// 从原始密匙数据创建一个DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);

		// 创建一个密匙工厂，然后用它把DESKeySpec对象转换成 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(dks);

		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance(ALGORITHM);

		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey);

		// 正式执行解密操作
		return cipher.doFinal(src);

	}

	public final static byte[] decryptString(String data, byte[] key) {
		try {
			return decrypt(StringUtil.hex2byte(data.getBytes()), key);
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}

	private static byte[] padding(byte[] sourceBytes, byte b) {
		// 补足8位
		int offset = sourceBytes.length % 8;
		if (offset == 0) {
			return sourceBytes;
		}

		int paddingSize = 8 - offset;
		byte[] paddingBytes = new byte[paddingSize];
		for (int i = 0; i < paddingBytes.length; i++) {
			paddingBytes[i] = b;
		}
		sourceBytes = ArrayUtils.addAll(sourceBytes, paddingBytes);
		return sourceBytes;
	}

	public static void main(String[] argv) throws Exception {

		String source = "Don't tell anybody!";
		byte[] key = genKey();
		System.out.println("key: " + ArrayUtils.toString(key));

		byte[] encrypted = DES.encrypt(source.getBytes(), key);
		byte[] decrypted = DES.decrypt(encrypted, key);

		System.out.println("source:" + source);
		System.out.println("encrypted:" + StringUtil.byte2Hex(encrypted));
		System.out.println("decrypted:" + new String(decrypted));

	}
}
