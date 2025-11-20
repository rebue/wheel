package rebue.wheel.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static rebue.wheel.api.cst.RegexCst.*;

public class RegexUtils {
    /**
     * 列出分组信息
     *
     * @param pattern 正则模式
     * @param text    要查找的文本
     * @return 分组列表(如果没有匹配 ， 返回null)
     */
    public static List<String> listGroup(Pattern pattern, String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        List<String> group      = new ArrayList<>();
        Matcher      matcher    = pattern.matcher(text);
        int          groupCount = matcher.groupCount();
        if (!matcher.find()) {
            return null;
        }
        try {
            for (int i = 1; i <= groupCount; i++) {
                group.add(matcher.group(i));
            }
        } catch (IllegalStateException e) {
            return null;
        }
        return group;
    }

    /**
     * 获取文本的首行
     *
     * @param text 文本
     * @return 首行，如果没有首行，返回null
     */
    public static String findFirstLine(final String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        Matcher matcher = FIRST_LINE.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 判断是否匹配手机号码的格式
     *
     * @param text 手机号码
     */
    public static boolean matchMobile(final String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        return MOBILE.matcher(text).matches();
    }

    /**
     * 判断是否匹配邮箱地址的格式
     *
     * @param text 判断是否匹配的文本
     */
    public static boolean matchEmail(final String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        return EMAIL.matcher(text).matches();
    }

    /**
     * 判断是否匹配身份证号码的格式
     *
     * @param text 判断是否匹配的文本
     */
    public static boolean matchIdCard(final String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        return ID_CARD.matcher(text).matches();
    }

    /**
     * 判断是否匹配IPv4:port的格式
     *
     * @param text 判断是否匹配的文本
     */
    public static boolean matchIpv4Port(final String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        return IPv4_PORT.matcher(text).matches();
    }

    /**
     * 判断是否匹配IPv4的格式
     *
     * @param text 判断是否匹配的文本
     */
    public static boolean matchIpv4(final String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        return IPv4.matcher(text).matches();
    }

    /**
     * 判断是否是局域网ip
     */
    public static boolean matchIpv4OfLan(final String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        return IPv4_OF_LOCAL.matcher(text).matches();
    }

    /**
     * 判断是否是十六进制
     */
    public static boolean matchHex(final String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        return HEX.matcher(text).matches();
    }

    /**
     * 判断是否是Base64编码
     */
    public static boolean matchBase64(final String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        return BASE64.matcher(text).matches();
    }

    /**
     * 判断是否是Base64Url编码
     */
    public static boolean matchBase64Url(final String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        return BASE64URL.matcher(text).matches();
    }


    public enum CarPlateType {
        /**
         * 大陆车牌（含使/领/警）
         */
        MAINLAND_ALL,
        /**
         * 大陆车牌（不含使/领）
         */
        MAINLAND,
        /**
         * 大陆车牌（不含使/领/警）
         */
        MAINLAND_NORMAL
    }

    /**
     * 判断是否匹配车牌号码
     */
    public static boolean matchCarPlate(final CarPlateType carPlateType, final String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        return switch (carPlateType) {
            case MAINLAND_ALL -> CAR_PLATE_MAINLAND_ALL.matcher(text).matches();
            case MAINLAND -> CAR_PLATE_MAINLAND.matcher(text).matches();
            case MAINLAND_NORMAL -> CAR_PLATE_MAINLAND_NORMAL.matcher(text).matches();
        };
    }
}
