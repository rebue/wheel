package rebue.wheel.core;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdCardValidator {
    private static Logger _log = LoggerFactory.getLogger(IdCardValidator.class);

    // 18位身份证中，各个数字的生成校验码时的权值
    private final static int[]  VERIFY_CODE_WEIGHT = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
    // 18位身份证中最后一位校验码
    private final static char[] VERIFY_CODE        = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };

    // private static Map<Integer, String> _addresses = new HashMap<Integer, String>();

    // TODO joda-time解析日期非常严重的bug
    // // 身份证号码中的出生日期的格式
    // private final static DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyyMMdd");
    // // 身份证的最小出生日期,1900年1月1日
    // private final static long MINIMAL_BORN_DATE = new DateTime(1900, 1, 1, 0, 0).getMillis();

    // 时间解析器
    private static TimeParseFixer _timeParseFixer;

    // 身份证的最小出生日期,1900年1月1日
    private static Date MINIMAL_BORN_DATE;

    static {
        _timeParseFixer = new TimeParseFixer("yyyyMMdd");
        try {
            MINIMAL_BORN_DATE = _timeParseFixer.parse("19000101");
        } catch (final ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证身份证号码是否正确
     * 
     * @param sNumber
     *            要验证的身份证号码
     */
    public static boolean validate(final String sNumber) {
        if (StringUtils.isBlank(sNumber)) {
            _log.error("身份证号码为空！");
            return false;
        }

        // 正则验证
        if (!RegexUtils.matchIdcard(sNumber)) {
            _log.error("身份证号码格式不正确：{}", sNumber);
            return false;
        }

        // 出生日期不能晚于当前时间，并且不能早于1900年
        final String sBirthday = sNumber.substring(6, 14);
        // TODO joda-time解析日期非常严重的bug
        // DateTime birthday;
        // try {
        // birthday = DATE_FORMAT.parseDateTime(sBirthday + " 11");
        // } catch (IllegalArgumentException e) {
        // _logger.error("身份证号码中的日期格式不正确：" + sNumber);
        // return false;
        // }
        // if (birthday.isBefore(MINIMAL_BORN_DATE) || birthday.isAfterNow()) {
        // _logger.error("身份证号码中的日期小于" + new DateTime(MINIMAL_BORN_DATE) + "或者大于当前时间：" + sNumber);
        // return false;
        // }
        Date birthday;
        try {
            birthday = _timeParseFixer.parse(sBirthday);
        } catch (final ParseException e) {
            _log.error("身份证号码中的日期格式不正确：" + sNumber);
            return false;
        }
        if (birthday.before(MINIMAL_BORN_DATE) || birthday.after(new Date())) {
            try {
                _log.error("身份证号码中的日期小于" + _timeParseFixer.formatDate(MINIMAL_BORN_DATE) + "或者大于当前时间：" + sNumber);
            } catch (final ParseException e) {
                e.printStackTrace();
            }
            return false;
        }

        // 验证校验码
        char ch = sNumber.charAt(17);
        if (ch == 'x') {
            ch = 'X';
        }
        if (ch != calcCheckBit(sNumber)) {
            _log.error("身份证号码中的校验码不正确：" + sNumber);
            return false;
        }

        return true;
    }

    /**
     * 计算校验位
     */
    public static char calcCheckBit(final String idCardNumber) {
        int sum = 0;
        final int iNumberLength = 17;
        for (int i = 0; i < iNumberLength; i++) {
            sum += (idCardNumber.charAt(i) - '0') * VERIFY_CODE_WEIGHT[i];
        }
        return VERIFY_CODE[sum % 11];
    }

}
