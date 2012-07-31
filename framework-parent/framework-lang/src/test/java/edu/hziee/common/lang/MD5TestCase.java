/**
 * 
 */
package edu.hziee.common.lang;

import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

/**
 * @author Administrator
 * 
 */
public class MD5TestCase {

	@Test
	public void testEncrypt() {
		byte[] key = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
		byte[] src = new byte[10 * 1024 * 1024];
		for (int i = 0; i < src.length; i++) {
			src[i] = (byte) i;
		}
		Date startTime = new Date();
		byte[] val = MD5.encrypt(src, key);
		Date endTime = new Date();
		System.out.println("cost: " + (endTime.getTime() - startTime.getTime()) + "ms");
		System.out.println(ArrayUtils.toString(val));
	}

}
