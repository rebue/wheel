package rebue.wheel.api.constant;

import java.util.regex.Pattern;

public class RegexConstant {
    /**
     * 首行
     */
    public static final Pattern FIRST_LINE    = Pattern.compile("^(.+)\n*");
    /**
     * 手机号码
     */
    public static final Pattern MOBILE        = Pattern.compile("1\\d{10}");
    /**
     * 邮箱地址
     * name@domain
     * name最长64，domain最长253，总长最长256 name可以使用任意ASCII字符:
     * 大小写英文字母 a-z,A-Z
     * 数字 0-9
     * 字符 !#$%&amp;'*+-/=?^_`{|}~
     * 字符 .不能是第一个和最后一个，不能连续出现两次 但是有些邮件服务器会拒绝包含有特殊字符的邮件地址
     * domain仅限于26个英文字母、10个数字、连词号- 连词号-不能是第一个字符
     * 顶级域名（com、cn等）长度为2到6个
     */
    public static final Pattern EMAIL         = Pattern.compile("[\\da-zA-Z].{0,63}@[\\da-zA-Z][\\da-zA-Z.-]{0,252}");
    /**
     * 身份证号
     */
    public static final Pattern ID_CARD       = Pattern.compile("[1-9]\\d{5}(19|20)\\d{2}((0[1-9])|(1[012]))((0[1-9])|([12]\\d)|(3[01]))\\d{3}[\\dXx]");
    /**
     * IP:port地址(IPv4)
     */
    public static final Pattern IPv4_PORT     = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}:\\d{1,5}$");
    /**
     * IP地址(IPv4)
     */
    public static final Pattern IPv4          = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3}$");
    /**
     * 局域网IP地址(IPv4)
     */
    public static final Pattern IPv4_OF_LOCAL = Pattern.compile(
            "(10|172|192)\\.([0-1]\\d{0,2}|2[0-5]{0,2}|[3-9]\\d?)\\.([0-1]\\d{0,2}|2[0-5]{0,2}|[3-9]\\d?)\\.([0-1]\\d{0,2}|2[0-5]{0,2}|[3-9]\\d?)");
    /**
     * 16进制字符串
     */
    public static final Pattern HEX           = Pattern.compile("^[\\da-fA-F]+$");
    /**
     * Base64字符串
     * 在Base64编码中，字符集是[A-Z, a-z, 0-9, +, /, =]
     * 如果剩余长度小于4，则使用'='字符填充字符串
     */
    public static final Pattern BASE64        = Pattern.compile("^([A-Za-z\\d+/]{4})*([A-Za-z\\d+/]{3}=|[A-Za-z\\d+/]{2}==)?$");
    /**
     * Base64Url字符串
     * 在Base64uUrl编码中，字符集是[A-Z, a-z, 0-9, -, _]
     */
    public static final Pattern BASE64URL     = Pattern.compile("^[A-Za-z\\d-_]+$");
}
