package rebue.wheel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class DateTests {
    /**
     * 测试计算添加小时时，HOUR_OF_DAY和HOUR的区别
     */
    @Test
    public void test01() {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 添加小时
        final Date now = new Date();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR_OF_DAY, 169);    // 添加169个小时
        final Date date1 = calendar.getTime();
        System.out.println(sdf.format(date1));

        calendar.setTime(now);
        calendar.add(Calendar.HOUR, 169);    // 添加169个小时
        final Date date2 = calendar.getTime();
        System.out.println(sdf.format(date2));

        Assert.assertEquals(date1, date2);

        System.out.println("结论：计算添加小时用HOUR_OF_DAY或HOUR，结果一样");
    }

}
