package rebue.wheel;

public class NumberUtils {
    /**
     * 判断字符串是否有效的Long类型
     * 
     * @param str 要判断的字符串
     * 
     * @return 是否有效的Long类型
     */
    public static boolean isValidLong(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否有效的Integer类型
     *
     * @param str 要判断的字符串
     *
     * @return 是否有效的Integer类型
     */
    public static boolean isValidInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
