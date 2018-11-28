package rebue.wheel;

import java.math.BigDecimal;

import org.junit.Test;

public class BigDecimalTests {
    /**
     * intValue只会截取整数部分，需要先setScale四舍五入
     */
    @Test
    public void test01() {
        final BigDecimal one = BigDecimal.valueOf(1.11);
        System.out.println(one.intValue());
        System.out.println(one.multiply(BigDecimal.valueOf(60)).intValue());
        final BigDecimal two = BigDecimal.valueOf(0.05);
        System.out.println(two.intValue());
        System.out.println(two.multiply(BigDecimal.valueOf(60)).intValue());
        final BigDecimal three = BigDecimal.valueOf(1.33);
        System.out.println(three.intValue());
        System.out.println(three.multiply(BigDecimal.valueOf(60)).intValue());
        final BigDecimal four = BigDecimal.valueOf(1.33);
        System.out.println(four.intValue());
        System.out.println(four.multiply(BigDecimal.valueOf(60)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
    }
}
