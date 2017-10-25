package com.zboss.wheel;

import java.util.regex.Pattern;

public class RegexUtils {
    /**
     * 验证手机号码是否符合格式
     * 
     * @param mobile
     *            手机号码
     */
    public static boolean matchMobile(String mobile) {
        return Pattern.matches("1\\d{10}", mobile);
    }

    /**
     * 验证邮箱地址是否符合格式
     * 
     * @param email
     *            邮箱地址<br>
     *            Email的规则: name@domain<br>
     *            name最长64，domain最长253，总长最长256 name可以使用任意ASCII字符: <br>
     *            大小写英文字母 a-z,A-Z<br>
     *            数字 0-9<br>
     *            字符 !#$%&'*+-/=?^_`{|}~ <br>
     *            字符 .不能是第一个和最后一个，不能连续出现两次 但是有些邮件服务器会拒绝包含有特殊字符的邮件地址
     *            domain仅限于26个英文字母、10个数字、连词号- 连词号-不能是第一个字符<br>
     *            顶级域名（com、cn等）长度为2到6个
     */
    public static boolean matchEmail(String email) {
        return Pattern.matches("[0-9a-zA-Z].{0,63}@[0-9a-zA-Z][0-9a-zA-Z\\.-]{0,252}", email);
    }

}
