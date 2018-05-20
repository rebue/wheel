package rebue.wheel;

import java.util.regex.Pattern;

public class RegexUtils {
    private static final Pattern MOBILE  = Pattern.compile("1\\d{10}");
    private static final Pattern EMAIL   = Pattern.compile("[0-9a-zA-Z].{0,63}@[0-9a-zA-Z][0-9a-zA-Z\\.-]{0,252}");
    private static final Pattern IDCARD  = Pattern.compile("[1-9]\\d{5}(19|20)\\d{2}((0[1-9])|(1[012]))((0[1-9])|([12]\\d)|(3[01]))\\d{3}[0-9Xx]");
    /**
     * IP:port地址(包括IPv4和IPv6)
     */
    private static final Pattern IP_PORT = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3,5}\\:\\d{1,5}$");
    /**
     * IP地址(包括IPv4和IPv6)
     */
    private static final Pattern IP      = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    /**
     * 判断是否匹配手机号码的格式
     * 
     * @param text
     *            手机号码
     */
    public static boolean matchMobile(String text) {
        return MOBILE.matcher(text).matches();
    }

    /**
     * 判断是否匹配邮箱地址的格式
     * 
     * @param text
     *            判断是否匹配的文本
     *            邮箱地址Email的规则: name@domain
     *            name最长64，domain最长253，总长最长256 name可以使用任意ASCII字符:
     *            大小写英文字母 a-z,A-Z
     *            数字 0-9
     *            字符 !#$%&amp;'*+-/=?^_`{|}~
     *            字符 .不能是第一个和最后一个，不能连续出现两次 但是有些邮件服务器会拒绝包含有特殊字符的邮件地址
     *            domain仅限于26个英文字母、10个数字、连词号- 连词号-不能是第一个字符
     *            顶级域名（com、cn等）长度为2到6个
     */
    public static boolean matchEmail(String text) {
        return EMAIL.matcher(text).matches();
    }

    /**
     * 判断是否匹配身份证号码的格式
     * 
     * @param text
     *            判断是否匹配的文本
     */
    public static boolean matchIdcard(String text) {
        return IDCARD.matcher(text).matches();
    }

    /**
     * 判断是否匹配ip:port的格式(包括IPv4和IPv6)
     * 
     * @param text
     *            判断是否匹配的文本
     */
    public static boolean matchIpPort(String text) {
        return IP_PORT.matcher(text).matches();
    }

    /**
     * 判断是否匹配ip的格式(包括IPv4和IPv6)
     * 
     * @param text
     *            判断是否匹配的文本
     */
    public static boolean matchIp(String text) {
        return IP.matcher(text).matches();
    }
}
