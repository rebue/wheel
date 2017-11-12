package rebue.wheel;

import java.util.Calendar;

public class DateUtils {
    public static java.sql.Date toSqlDate(java.util.Date date) {
        if (date != null)
            return new java.sql.Date(date.getTime());
        else
            return null;
    }

    /**
     * 计算到明天0时的毫秒数
     * 
     * @return 到明天0时的毫秒数
     */
    public static int msUtilTomorrow() {
        // 计算明天零时
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 计算到明天零时的毫秒数(加入黑名单的时间)
        return (int) (calendar.getTimeInMillis() - System.currentTimeMillis());
    }

    /**
     * 计算到明天0时的秒数
     * 
     * @return 到明天0时的秒数
     */
    public static int secondUtilTomorrow() {
        return msUtilTomorrow() / 1000;
    }

}
