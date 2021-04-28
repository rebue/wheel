package rebue.wheel.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateUtils {
    public static java.sql.Date toSqlDate(final java.util.Date date) {
        if (date != null) {
            return new java.sql.Date(date.getTime());
        } else {
            return null;
        }
    }

    /**
     * 计算到明天0时的毫秒数
     * 
     * @return 到明天0时的毫秒数
     */
    public static int getMsUtilTomorrow() {
        // 计算明天零时
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 计算到明天零时的毫秒数
        return (int) (calendar.getTimeInMillis() - System.currentTimeMillis());
    }

    /**
     * 计算到明天0时的秒数
     * 
     * @return 到明天0时的秒数
     */
    public static int getSecondUtilTomorrow() {
        return getMsUtilTomorrow() / 1000;
    }

    private static final String  errmsg         = "String转换Date失败，String格式不正确:";
    private static final Pattern DATE_PATTERN   = Pattern.compile("^\\d{4}\\-(0[1-9]|1[0-2])\\-(0[1-9]|[12][0-9]|3[01])$");
    private static final Pattern TIME_PATTERN   = Pattern.compile("^\\d{4}\\-(0[1-9]|1[0-2])\\-(0[1-9]|[12][0-9]|3[01])\\s([01][0-9]|2[0-4]):[0-5][0-9]:[0-5][0-9]");
    private static final Pattern MSTIME_PATTERN = Pattern.compile("^\\d{4}\\-(0[1-9]|1[0-2])\\-(0[1-9]|[12][0-9]|3[01])\\s([01][0-9]|2[0-4]):[0-5][0-9]:[0-5][0-9]\\.\\d{3}");
    private static final Pattern ZDATE_PATTERN  = Pattern.compile("^\\d{4}\\-(0[1-9]|1[0-2])\\-(0[1-9]|[12][0-9]|3[01])T16:00:00\\.000Z");
    private static final Pattern ZTIME_PATTERN  = Pattern.compile("^\\d{4}\\-(0[1-9]|1[0-2])\\-(0[1-9]|[12][0-9]|3[01])T([01][0-9]|2[0-4]):[0-5][0-9]:[0-5][0-9]\\.\\d{3}Z");

    /**
     * String转Date
     */
    public static Date stringToDate(final String sDate) throws ParseException {
        if (StringUtils.isBlank(sDate)) {
            return null;
        }
        String format = "";
        try {
            if (DATE_PATTERN.matcher(sDate).matches()) {
                format = "yyyy-MM-dd";
            } else if (TIME_PATTERN.matcher(sDate).matches()) {
                format = "yyyy-MM-dd HH:mm:ss";
            } else if (MSTIME_PATTERN.matcher(sDate).matches()) {
                format = "yyyy-MM-dd HH:mm:ss.SSS";
            } else if (ZDATE_PATTERN.matcher(sDate).matches()) {
                format = "yyyy-MM-dd";
            } else if (ZTIME_PATTERN.matcher(sDate).matches()) {
                format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            } else {
                throw new ParseException(errmsg + sDate, 0);
            }
            final SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(sDate);
        } catch (final ParseException e) {
            log.error(errmsg + sDate);
            throw e;
        }
    }

}
