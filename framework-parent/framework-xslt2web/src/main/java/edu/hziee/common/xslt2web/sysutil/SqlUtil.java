package edu.hziee.common.xslt2web.sysutil;

import java.io.UnsupportedEncodingException;

import edu.hziee.common.xslt2web.provider.GlobalProvider;
import edu.hziee.common.xslt2web.sys.CodeSearchType;

public final class SqlUtil {

	private SqlUtil() {
	}

	private final static String[] startChars = { "°¡", "°Å", "²Á", "´î", "¶ê", "·¢",
			"¸Á", "¹þ", "»÷", "»÷", "¿¦", "À¬", "Âè", "ÄÃ", "Å¶", "Å¾", "ÆÚ", "È»", "Èö",
			"Ëú", "ÍÚ", "ÍÚ", "ÍÚ", "Îô", "Ñ¹", "ÔÑ" };
	private final static String[] endChars = { "°Ä", "²À", "´í", "¶é", "·¡", "¸À",
			"¹ý", "»ö", "°¡", "¿¥", "À«", "Âç", "ÄÂ", "Åµ", "Å½", "ÆÙ", "Èº", "Èõ", "Ëù",
			"ÍÙ", "°¡", "°¡", "Îó", "Ñ¸", "ÔÐ", "×ù" };

	public static String getCharFullCondition(String fieldName,
			String fieldValue) {
		StringBuilder sql = new StringBuilder(1024);
		int i = 1;
		char[] charArr = fieldValue.toCharArray();
		for (char c : charArr) {
			if (i > 1)
				sql.append(" AND ");
			int index = (int) (Character.toUpperCase(c)) - (int) 'A';
			String startWord, endWord;
			if (index >= 0 && index < 26) {
				startWord = startChars[index];
				endWord = endChars[index];
			} else {
				startWord = startChars[0];
				endWord = endChars[0];
			}
			String subStr = GlobalProvider.getSqlProvider().getFunction(
					"SUBSTRING", fieldName, i, 1);
			sql.append(String.format("(%s BETWEEN '%s' AND '%s')", subStr,
					startWord, endWord));
			++i;
		}

		return sql.toString();
	}

	private final static int[] startBytes = { 45217, 45253, 45761, 46318,
			46826, 47010, 47297, 47614, 48119, 48119, 49062, 49324, 49896,
			50371, 50614, 50622, 50906, 51387, 51446, 52218, 52698, 52698,
			52698, 52980, 53689, 54481 };
	private final static int[] endBytes = { 45252, 45760, 46317, 46825, 47009,
			47296, 47613, 48118, 45217, 49061, 49323, 49895, 50370, 50613,
			50621, 50905, 51386, 51445, 52217, 52697, 45217, 45217, 52979,
			53640, 54480, 55289 };

	public static char getCharPY(char hz) {
		try {
			byte[] arr = Character.toString(hz).getBytes("GBK");
			if (arr.length > 1) {
				int temp = arr[0] << 8 + arr[1]; // * 256
				for (int i = 0; i < startBytes.length; ++i) {
					if (temp < startBytes[i])
						break;
					if (temp >= startBytes[i] && temp <= endBytes[i])
						return (char) (i + 'a');
				}
			}
		} catch (UnsupportedEncodingException e) {
		}
		return '\0';
	}

	public static String getPY(String hz) {
		StringBuilder result = new StringBuilder();
		char[] charArr = hz.toCharArray();
		for (char c : charArr) {
			char py = getCharPY(c);
			if (py != '\0')
				result.append(py);
		}
		return result.toString();
	}

	private final static char[] WIDE_CHARS = { '%', '_' };

	public static boolean hasWideChar(String value) {
		for (char c : WIDE_CHARS) {
			if (value.indexOf(c) > -1)
				return true;
		}
		return false;
	}

	public static CodeSearchType parseSearchValue(String value) {
		if (StringUtil.isEmpty(value))
			return CodeSearchType.CodeValue;

		boolean isChinese = false, isPy = true;
		for (int i = 0; i < value.length(); ++i) {
			char c = value.charAt(i);
			if ((int) c > 255) {
				isChinese = true;
				break;
			}
			char lower = Character.toLowerCase(c);
			if (lower < 'a' || lower > 'z') {
				isPy = false;
				break;
			}
		}
		if (isChinese)
			return CodeSearchType.CodeName;
		if (isPy)
			return CodeSearchType.CodePY;

		return CodeSearchType.CodeValue;
	}
}
