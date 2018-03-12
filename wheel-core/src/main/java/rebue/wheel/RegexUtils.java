package rebue.wheel;

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
     * @param email
     *            邮箱地址
     *            Email的规则: name@domain
     *            name最长64，domain最长253，总长最长256 name可以使用任意ASCII字符:
     *            大小写英文字母 a-z,A-Z
     *            数字 0-9
     *            字符 !#$%&amp;'*+-/=?^_`{|}~
     *            字符 .不能是第一个和最后一个，不能连续出现两次 但是有些邮件服务器会拒绝包含有特殊字符的邮件地址
     *            domain仅限于26个英文字母、10个数字、连词号- 连词号-不能是第一个字符
     *            顶级域名（com、cn等）长度为2到6个
     */
    public static boolean matchEmail(String email) {
        return Pattern.matches("[0-9a-zA-Z].{0,63}@[0-9a-zA-Z][0-9a-zA-Z\\.-]{0,252}", email);
    }

    /**
     * 验证身份证号码是否符合格式
     * 
     * @param idcard
     *            身份证号码
     */
    public static boolean matchIdcard(String idcard) {
        return Pattern.matches("[1-9]\\d{5}(19|20)\\d{2}((0[1-9])|(1[012]))((0[1-9])|([12]\\d)|(3[01]))\\d{3}[0-9Xx]",
                idcard);
    }
}
