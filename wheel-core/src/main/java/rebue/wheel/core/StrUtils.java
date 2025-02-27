package rebue.wheel.core;

public class StrUtils {
    /**
     * 判断字符在字符串中的数量是否超过限制
     * 
     * @param str        查找的字符串
     * @param targetChar 查找的字符
     * @param limit      限制字符的数量
     * @return 是否超过限制
     */
    public static boolean isCharCountOverLimit(String str, char targetChar, int limit) {
        long count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (targetChar == str.charAt(i)) {
                if (count == limit) {
                    return true;
                }
                count++;
            }
        }
        return false;
    }

    /**
     * 获取字符在字符串中的数量
     *
     * @param str        查找的字符串
     * @param targetChar 查找的字符
     * @return 字符在字符串中的数量
     */
    public static long count(String str, char targetChar) {
        return str.chars()
                .filter(c -> c == targetChar)
                .count();
    }

    /**
     * 首字母大写
     * 
     * @param str 要处理的字符串
     * @return 首字母大写的字符串
     */
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
