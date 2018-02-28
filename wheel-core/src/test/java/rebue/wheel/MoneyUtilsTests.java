package rebue.wheel;

import org.junit.Assert;
import org.junit.Test;

public class MoneyUtilsTests {

    @Test
    public void test01() {
        Assert.assertEquals(3170, MoneyUtils.yuan2fen(31.7));
        Assert.assertEquals(3145, MoneyUtils.yuan2fen(31.454));
        Assert.assertEquals(3145, MoneyUtils.yuan2fen(31.4549));
        Assert.assertEquals(3146, MoneyUtils.yuan2fen(31.455));
        Assert.assertEquals(3146, MoneyUtils.yuan2fen(31.4551));
        Assert.assertEquals(3146, MoneyUtils.yuan2fen(31.4599));
        Assert.assertEquals(15, MoneyUtils.yuan2fen(0.15));
        Assert.assertEquals(53880, MoneyUtils.yuan2fen(538.8));
        Assert.assertEquals(31.7, MoneyUtils.fen2yuan("3170"), 2);
        Assert.assertEquals(0.15, MoneyUtils.fen2yuan("15"), 2);
    }
}
