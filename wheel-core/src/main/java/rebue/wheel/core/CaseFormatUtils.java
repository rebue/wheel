package rebue.wheel.core;

import com.google.common.base.CaseFormat;

public class CaseFormatUtils {

    /**
     * 分割驼峰格式的字符串为数组
     * e.g., "splitCamel"->["split","Camel"]
     *
     * @param str 小驼峰格式的字符串
     * @return 烤串格式的字符串
     */
    public static String[] splitCamel(final String str) {
        return str.split("(?=[A-Z])");
    }

    /**
     * 将小驼峰格式的字符串转换成烤串格式的字符串
     * e.g., "lowerCame" -> "lower-hyphen"
     *
     * @param str 小驼峰格式的字符串
     * @return 烤串格式的字符串
     */
    public static String lowerCamelToLowerHyphen(final String str) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, str);
    }

    /**
     * 将大驼峰格式的字符串转换成烤串格式的字符串
     * e.g., "upperCamel" -> "lower-hyphen"
     * 
     * @param str 大驼峰格式的字符串
     * @return 烤串格式的字符串
     */
    public static String upperCamelToLowerHyphen(final String str) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, str);
    }

    /**
     * 将烤串格式的字符串转换成小驼峰格式的字符串
     * e.g., "lower-hyphen" -> "lowerCamel"
     *
     * @param str 小驼峰格式的字符串
     * @return 烤串格式的字符串
     */
    public static String lowerHyphenToLowerCamel(final String str) {
        return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, str);
    }
}
