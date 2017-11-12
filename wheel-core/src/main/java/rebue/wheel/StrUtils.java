/**
 * 
 */
package rebue.wheel;

import org.apache.commons.lang3.StringUtils;

/**
 * @author 张柏子
 * @creation 2013-5-4
 */
public final class StrUtils {

	/**
	 * <pre>
	 * 判断字符串是否是空 
	 * StrUtils.isEmpty(null) 	= true 
	 * StrUtils.isEmpty("")   	= true 
	 * StrUtils.isEmpty(" ")  	= false
	 * StrUtils.isEmpty("bob")  = false
	 * StrUtils.isEmpty("  bob  ") = false
	 * </pre>
	 */
	public static boolean isEmpty(String value) {
		return value == null || value.length() == 0;
	}

	/**
	 * <pre>
	 * 判断字符串是否是空 
	 * StrUtils.isEmpty(null) 	= true 
	 * StrUtils.isEmpty("")   	= true 
	 * StrUtils.isEmpty(" ")  	= true
	 * StrUtils.isEmpty("bob")  = false
	 * StrUtils.isEmpty("  bob  ") = false
	 * </pre>
	 */
	public static boolean isBlank(String value) {
		return value == null || value.trim().length() == 0;
	}

	/**
	 * 截取字符串左边指定长度的部分
	 */
	public static String left(String str, int count) {
		return str.substring(0, count);
	}

	/**
	 * 截取字符串右边指定长度的部分
	 * 
	 * @deprecated 使用{@link org.apache.commons.lang3.StringUtils#right}代替
	 */
	public static String right(String str, int count) {
		return str.substring(str.length() - count);
	}

	/**
	 * 删除字符串右边指定长度，返回剩余的部分
	 */
	public static String delRight(String str, int count) {
		return left(str, str.length() - count);
	}

	/**
	 * 获取重复字符的字符串
	 */
	public static String repeatChar(char repeatChar, int length) {
		String result = "";
		for (int i = 0; i < length; i++)
			result += repeatChar;
		return result;
	}

	/**
	 * 字符串左边补足字符
	 */
	public static String padLeft(String str, int length, char padChar) {
		if (str.length() >= length)
			return str;
		return repeatChar(padChar, length - str.length()) + str;
	}

	/**
	 * 字符串右边补足字符
	 */
	public static String padRight(String str, int length, char padChar) {
		if (str.length() >= length)
			return str;
		return str + repeatChar(padChar, length - str.length());
	}

	/**
	 * TODO 测试性能<br>
	 * 首字母大写
	 */
	public static String capitalize(String str) {
		return StringUtils.capitalize(str);
	}

	/**
	 * TODO 测试性能<br>
	 * 首字母小写
	 */
	public static String uncapitalize(String str) {
		return StringUtils.uncapitalize(str);
	}

	/**
	 * 首字母小写
	 */
	public static String uncapitalize2(String str) {
		StringBuilder sb = new StringBuilder();
		char ch = str.charAt(0);
		sb.append(Character.toLowerCase(ch));
		sb.append(str.substring(1));
		return sb.toString();
	}

	/**
	 * 首字母小写
	 */
	public static String uncapitalize3(String str) {
		char[] array = str.toCharArray();
		array[0] -= 32;
		return String.valueOf(array);
	}
}