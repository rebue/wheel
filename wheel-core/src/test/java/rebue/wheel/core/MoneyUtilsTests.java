package rebue.wheel.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MoneyUtilsTests {

    @Test
    public void test01() {
        Assertions.assertEquals(3170, MoneyUtils.yuan2fen(31.7));
        Assertions.assertEquals(3145, MoneyUtils.yuan2fen(31.454));
        Assertions.assertEquals(3145, MoneyUtils.yuan2fen(31.4549));
        Assertions.assertEquals(3146, MoneyUtils.yuan2fen(31.455));
        Assertions.assertEquals(3146, MoneyUtils.yuan2fen(31.4551));
        Assertions.assertEquals(3146, MoneyUtils.yuan2fen(31.4599));
        Assertions.assertEquals(15, MoneyUtils.yuan2fen(0.15));
        Assertions.assertEquals(53880, MoneyUtils.yuan2fen(538.8));
        Assertions.assertEquals(31.7, MoneyUtils.fen2yuan("3170"), 2);
        Assertions.assertEquals(0.15, MoneyUtils.fen2yuan("15"), 2);
    }
}
