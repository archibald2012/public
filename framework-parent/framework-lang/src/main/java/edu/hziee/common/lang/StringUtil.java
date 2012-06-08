
package edu.hziee.common.lang;

import java.util.Arrays;

/**
 * TODO
 * 
 * @author Administrator
 * @version $Id: StringUtil.java 14 2012-01-10 11:54:14Z archie $
 */
public class StringUtil {

	/**
	 * 将下划线连接的字符串替换为驼峰风格,方便JavaBean拷贝
	 * <p/>
	 * <h2>Example:</h2>
	 * <code>toCamelCasing("pic_path")</code> will return picPath
	 * 
	 * @param s
	 * @return
	 */
	public static String toCamelCasing(String s) {
		if (s == null) {
			return s;
		}

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < s.length() - 1; i++) {
			char ch = s.charAt(i);
			if (ch != '_') {
				buffer.append(ch);
			} else {
				char nextChar = s.charAt(i + 1);
				if (nextChar != '_') {
					if (buffer.toString().length() < 2) {
						buffer.append(Character.toLowerCase(nextChar));
					} else {
						buffer.append(Character.toUpperCase(nextChar));
					}
					i++;
				}
			}
		}
		char lastChar = s.charAt(s.length() - 1);
		if (lastChar != '_') {
			buffer.append(lastChar);
		}

		return buffer.toString();
	}

	public static boolean isBlank(String str) {
		int length;

		if ((str == null) || ((length = str.length()) == 0)) {
			return true;
		}

		for (int i = 0; i < length; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	public static boolean isEmpty(String str) {
		return ((str == null) || (str.length() == 0));
	}

	public static String rightPad(String text, int length, char c) {
		if (text == null) {
			return text;
		}
		if (text.length() >= length) {
			return text;
		}

		char[] array = new char[length];
		System.arraycopy(text.toCharArray(), 0, array, 0, text.length());
		Arrays.fill(array, text.length(), length, c);
		return new String(array);
	}
	
	/**
   * Turns an array of bytes into a String representing each byte as an
   * unsigned hex number.
   * <p/>
   * Method by Santeri Paavolainen, Helsinki Finland 1996<br>
   * (c) Santeri Paavolainen, Helsinki Finland 1996<br>
   * Distributed under LGPL.
   * 
   * @param hash
   *            an array of bytes to convert to a hex-string
   * @return generated hex string
   */
    public static String byte2Hex(byte hash[]) {
        StringBuffer buf = new StringBuffer(hash.length * 2);
        int i;

        for (i = 0; i < hash.length; i++) {
            if (((int) hash[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString((int) hash[i] & 0xff, 16));
        }
        return buf.toString();
    }

    public static byte[] hex2byte(byte[] b) {

        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("长度不是偶数");

        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

}
