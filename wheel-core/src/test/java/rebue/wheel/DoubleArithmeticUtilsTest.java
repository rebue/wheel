package rebue.wheel;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DoubleArithmeticUtilsTest {
    @Test
    public void test01() {
        double    a      = 33992.234234234;
        double    b      = 234.34532;
        final int scale  = 4;
        double    result = DoubleArithmeticUtils.add(a, b, scale);
        System.out.println(result);
        Assertions.assertEquals(34226.5796, result, 0);

        result = DoubleArithmeticUtils.mul(a, b, scale);
        System.out.println(result);
        Assertions.assertEquals(7965921.0091, result, 0);

        result = DoubleArithmeticUtils.divide(a, b, scale);
        System.out.println(result);
        Assertions.assertEquals(145.0519, result, 0);

        a      = 33992.2;
        b      = 234.3;
        result = DoubleArithmeticUtils.add(a, b, scale);
        System.out.println(result);
        Assertions.assertEquals(34226.5, result, 0);

        result = DoubleArithmeticUtils.mul(a, b, scale);
        System.out.println(result);
        Assertions.assertEquals(7964372.46, result, 0);

        result = DoubleArithmeticUtils.divide(a, b, scale);
        System.out.println(result);
        Assertions.assertEquals(145.0798, result, 0);
    }
}
