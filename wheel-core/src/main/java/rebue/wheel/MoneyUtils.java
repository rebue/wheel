package rebue.wheel;

import java.math.BigDecimal;

public class MoneyUtils {

    /**
     * 将以“分”为单位的金额转成以“元”为单位的金额
     * 
     * @param amount
     *            金额
     */
    public static Double fen2yuan(Integer amount) {
        BigDecimal value = new BigDecimal(amount);
        return value.divide(new BigDecimal(100)).doubleValue();
    }

    public static Double fen2yuan(String amount) {
        BigDecimal value = new BigDecimal(amount);
        return value.divide(new BigDecimal(100)).doubleValue();
    }

    /**
     * 将以“元”为单位的金额转成以“分”为单位的金额
     * 
     * @param amount
     *            金额
     */
    public static Integer yuan2fen(Double amount) {
        BigDecimal value = new BigDecimal(amount);
        return value.multiply(new BigDecimal(100)).intValue();
    }

    public static Integer yuan2fen(String amount) {
        BigDecimal value = new BigDecimal(amount);
        return value.multiply(new BigDecimal(100)).intValue();
    }

}
