package rebue.wheel.api.constant;

import java.util.regex.Pattern;

public class RegexConstant {
    public static final Pattern MOBILE        = Pattern.compile("1\\d{10}");
    public static final Pattern EMAIL         = Pattern.compile("[0-9a-zA-Z].{0,63}@[0-9a-zA-Z][0-9a-zA-Z\\.-]{0,252}");
    public static final Pattern ID_CARD       = Pattern.compile("[1-9]\\d{5}(19|20)\\d{2}((0[1-9])|(1[012]))((0[1-9])|([12]\\d)|(3[01]))\\d{3}[0-9Xx]");
    /**
     * IP:port地址(IPv4)
     */
    public static final Pattern IPv4_PORT     = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}\\:\\d{1,5}$");
    /**
     * IP地址(IPv4)
     */
    public static final Pattern IPv4          = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3}$");
    /**
     * 局域网IP地址(IPv4)
     */
    public static final Pattern IPv4_OF_LOCAL = Pattern.compile(
            "(10|172|192)\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})");

}
