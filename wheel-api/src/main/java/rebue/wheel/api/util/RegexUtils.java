package rebue.wheel.api.util;

import static rebue.wheel.api.constant.RegexConstant.*;

public class RegexUtils {
    /**
     * 判断是否匹配手机号码的格式
     *
     * @param text 手机号码
     */
    public static boolean matchMobile(final String text) {
        return MOBILE.matcher(text).matches();
    }

    /**
     * 判断是否匹配邮箱地址的格式
     *
     * @param text 判断是否匹配的文本
     */
    public static boolean matchEmail(final String text) {
        return EMAIL.matcher(text).matches();
    }

    /**
     * 判断是否匹配身份证号码的格式
     *
     * @param text 判断是否匹配的文本
     */
    public static boolean matchIdCard(final String text) {
        return ID_CARD.matcher(text).matches();
    }

    /**
     * 判断是否匹配IPv4:port的格式
     *
     * @param text 判断是否匹配的文本
     */
    public static boolean matchIpv4Port(final String text) {
        return IPv4_PORT.matcher(text).matches();
    }

    /**
     * 判断是否匹配IPv4的格式
     *
     * @param text 判断是否匹配的文本
     */
    public static boolean matchIpv4(final String text) {
        return IPv4.matcher(text).matches();
    }

    /**
     * 判断是否是局域网ip
     */
    public static boolean matchIpv4OfLan(final String text) {
        return IPv4_OF_LOCAL.matcher(text).matches();
    }

    /**
     * 判断是否是十六进制
     */
    public static boolean matchHex(final String text) {
        return HEX.matcher(text).matches();
    }

    /**
     * 判断是否是Base64编码
     */
    public static boolean matchBase64(final String text) {
        return BASE64.matcher(text).matches();
    }

    /**
     * 判断是否是Base64Url编码
     */
    public static boolean matchBase64Url(final String text) {
        return BASE64URL.matcher(text).matches();
    }


}
