package rebue.wheel;

import java.math.BigDecimal;

import org.junit.Test;

public class BigDecimalTests {
    @Test
    public void test01() {
        final BigDecimal one = BigDecimal.valueOf(1.11);
        System.out.println(one.intValue());
        System.out.println(one.multiply(BigDecimal.valueOf(60)).intValue());
        final BigDecimal two = BigDecimal.valueOf(0.05);
        System.out.println(two.intValue());
        System.out.println(two.multiply(BigDecimal.valueOf(60)).intValue());
    }
}
