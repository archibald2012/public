package edu.hziee.common.lang;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.spec.DHParameterSpec;

/**
 * 迪菲－赫尔曼密钥交换（Diffie–Hellman key exchange，简称“D–H”）
 * 是一种安全协议。它可以让双方在完全没有对方任何预先信息的条件下通过不安全信道建立起一个密钥。这个密钥可以在后续的通讯中作为对称密钥来加密通讯内容
 * 
 * @author Administrator
 * 
 */
public class DH {

	/**
	 * 
	 * @param primeV
	 *          the prime modulus
	 * @param baseV
	 *          the base generator
	 * @param privateV
	 *          the size in bits of the random exponent (private value)
	 * @return
	 * @throws Exception
	 */
	public static KeyPair genKey(BigInteger primeModules, BigInteger baseGenerator, int privateValue) throws Exception {
		// Use the values to generate a key pair
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
		DHParameterSpec dhSpec = new DHParameterSpec(primeModules, baseGenerator, privateValue);
		keyGen.initialize(dhSpec);
		return keyGen.generateKeyPair();
	}

	public static PublicKey decodePublicKey(byte[] key) throws Exception {
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance("DH");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}

	public static PrivateKey decodePrivateKey(byte[] key) throws Exception {
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance("DH");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}
}
