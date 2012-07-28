/**
 * 
 */
package edu.hziee.common.lang;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.lang.ArrayUtils;

/**
 * @author Administrator
 * 
 */
public class RSA {

	// 只有PKCS1PADDING可以直接还原原始数据，
	// NOPadding导致解压出来的都是blocksize长度的数据，还要自己处理
	private static final String	ALGORITHM	= "RSA/ECB/PKCS1PADDING";

	private static final int		KEY_SIZE	= 2048;

	public static byte[] encrypt(byte[] src, Key key) {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(src);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] decrypt(byte[] src, Key key) {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(src);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static PublicKey decodePublicKey(byte[] key) {
		try {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static PrivateKey decodePrivateKey(byte[] key) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(key));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static KeyPair genKey() {
		return genKey(null);
	}

	public static KeyPair genKey(String seed) {
		try {
			KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
			if (seed != null && seed.length() > 0) {
				SecureRandom random = new SecureRandom();
				random.setSeed(seed.getBytes());
				keygen.initialize(KEY_SIZE, random);
			} else {
				keygen.initialize(KEY_SIZE);
			}
			KeyPair keys = keygen.generateKeyPair();
			return keys;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] sign(byte[] src, PrivateKey privateKey) {
		try {
			// 实例化Signature，用于产生数字签名，指定用RSA和SHA算法
			Signature sig = Signature.getInstance("SHA1WithRSA");
			// 用私钥来初始化数字签名对象
			sig.initSign(privateKey);
			// 对src实施签名
			sig.update(src);
			// 完成签名
			return sig.sign();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean verify(byte[] src, PublicKey publicKey, byte[] sign) {
		try {
			// 实例化Signature，用于产生数字签名，指定用RSA和SHA算法
			Signature sig = Signature.getInstance("SHA1WithRSA");
			sig.initVerify(publicKey);
			// 对src重新实施签名
			sig.update(src);
			return sig.verify(sign);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws Exception {

		String str = "Don't tell any body!";
		System.out.println("source: " + str);

		String seed = "helloworld";
		KeyPair keyPair = genKey(seed);
		PublicKey publicKey = keyPair.getPublic();
		System.out.println(publicKey.getEncoded().length);
		System.out.println(ArrayUtils.toString(publicKey.getEncoded()));
		PrivateKey privateKey = keyPair.getPrivate();
		System.out.println(privateKey.getEncoded().length);
		System.out.println(ArrayUtils.toString(privateKey.getEncoded()));

		byte[] encrypted = encrypt(str.getBytes(), publicKey);
		System.out.println("public key encrypted: " + ArrayUtils.toString(encrypted));

		byte[] decrypted = decrypt(encrypted, privateKey);
		System.out.println("private key decrypted: " + ArrayUtils.toString(decrypted));

		System.out.println("target: " + new String(decrypted));

		encrypted = encrypt(str.getBytes(), privateKey);
		System.out.println("private key encrypted: " + ArrayUtils.toString(encrypted));

		decrypted = decrypt(encrypted, publicKey);
		System.out.println("public key decrypted: " + ArrayUtils.toString(decrypted));

		System.out.println("target: " + new String(decrypted));
	}
}
