package rebue.wheel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateTests {
    /**
     * 测试计算添加小时时，HOUR_OF_DAY和HOUR的区别
     */
    @Test
    @Disabled
    public void test01() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 添加小时
        final Date now = new Date();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR_OF_DAY, 169);    // 添加169个小时
        final Date date1 = calendar.getTime();
        log.info(sdf.format(date1));

        calendar.setTime(now);
        calendar.add(Calendar.HOUR, 169);    // 添加169个小时
        final Date date2 = calendar.getTime();
        log.info(sdf.format(date2));

        Assertions.assertEquals(date1, date2);

        log.info("结论：计算添加小时用HOUR_OF_DAY或HOUR，结果一样");
    }

    @Test
    public void testStringToDate() throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String sDate = "1981-05-22";
        log.info(sdf.format(DateUtils.stringToDate(sDate)));
        sDate = "1981-05-22 16:15:12";
        log.info(sdf.format(DateUtils.stringToDate(sDate)));
        sDate = "1981-05-22 16:23:22.223";
        log.info(sdf.format(DateUtils.stringToDate(sDate)));
        sDate = "1981-05-22T16:00:00.000Z";
        log.info(sdf.format(DateUtils.stringToDate(sDate)));
        sDate = "1985-02-28T17:13:17.123Z";
        log.info(sdf.format(DateUtils.stringToDate(sDate)));
    }

}
