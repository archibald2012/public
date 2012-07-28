package edu.hziee.common.lang;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import junit.framework.Assert;

import org.apache.commons.lang.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RSATestCase {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSign() throws Exception {
		byte[] src = "xxxx".getBytes();
		System.out.println(ArrayUtils.toString(src));
		KeyPair keyPair = RSA.genKey();

		byte[] sign = RSA.sign(src, keyPair.getPrivate());
		System.out.println(ArrayUtils.toString(sign));

		Assert.assertTrue(RSA.verify(src, keyPair.getPublic(), sign));
	}

	@Test
	public void testGenKey() throws Exception {
		String seed = "helloworld";
		KeyPair keyPair = RSA.genKey(seed);
		PublicKey publicKey = keyPair.getPublic();
		System.out.println(publicKey.getEncoded().length);
		System.out.println(ArrayUtils.toString(publicKey.getEncoded()));
		PrivateKey privateKey = keyPair.getPrivate();
		System.out.println(privateKey.getEncoded().length);
		System.out.println(ArrayUtils.toString(privateKey.getEncoded()));
	}

	@Test
	public void testEncrypt() throws Exception {

		String seed = "helloworld";
		KeyPair keyPair = RSA.genKey(seed);
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		String str = "Don't tell any body!";
		System.out.println("source: " + str);

		byte[] encrypted = RSA.encrypt(str.getBytes(), publicKey);
		System.out.println("public key encrypted: " + ArrayUtils.toString(encrypted));

		byte[] decrypted = RSA.decrypt(encrypted, privateKey);
		System.out.println("private key decrypted: " + ArrayUtils.toString(decrypted));

		System.out.println("target: " + new String(decrypted));

		Assert.assertEquals(new String(decrypted), str);
	}

}
