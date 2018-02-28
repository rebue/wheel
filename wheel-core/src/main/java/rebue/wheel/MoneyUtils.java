package rebue.wheel;

import java.math.BigDecimal;

public class MoneyUtils {

    /**
     * 将以“分”为单位的金额转成以“元”为单位的金额
     */
    public static double fen2yuan(String amount) {
        BigDecimal value = new BigDecimal(amount);
        return value.divide(new BigDecimal(100)).doubleValue();
    }

    /**
     * 将以“元”为单位的金额转成以“分”为单位的金额
     */
    public static int yuan2fen(Double amount) {
        BigDecimal value = new BigDecimal(amount.toString()).multiply(new BigDecimal(100));
        return value.divide(new BigDecimal("1"), 0, BigDecimal.ROUND_HALF_UP).intValue();   // 四余五入保留整数
    }

}
