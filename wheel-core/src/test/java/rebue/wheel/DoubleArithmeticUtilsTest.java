package rebue.wheel;

import org.junit.Test;

public class DoubleArithmeticUtilsTest {
    @Test
    public void test01() {
        double a = 33992.234234234;
        double b = 234.34532;
        int scale = 4;
        System.out.println(DoubleArithmeticUtils.add(a, b, scale));
    }
}
