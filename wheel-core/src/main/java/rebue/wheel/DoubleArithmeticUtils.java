package rebue.wheel;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * 浮点运算(主要是对BigDecimal类的使用提供示范)
 * XXX wheel : 浮点运算: 注意在构造BigDecimal时double类型要先转为整形，而不能直接构造
 * 
 * @author zbz
 *
 */
public class DoubleArithmeticUtils {
    /**
     * 提供精确的加法运算。
     */
    public static double add(Double value1, Double value2) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));    // 注意: 在构造BigDecimal时Double类型要先转为字符串类型
        BigDecimal b2 = new BigDecimal(Double.toString(value2));    // 注意: 在构造BigDecimal时Double类型要先转为字符串类型
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供(相对)精确的加法运算，精确到小数点以后scale位，以后的数字四舍五入。
     */
    public static double add(Double value1, Double value2, int scale) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));    // 注意: 在构造BigDecimal时Double类型要先转为字符串类型
        BigDecimal b2 = new BigDecimal(Double.toString(value2));    // 注意: 在构造BigDecimal时Double类型要先转为字符串类型
        return b1.add(b2, new MathContext(scale)).doubleValue();
    }

    /**
     * 提供精确的减法运算。
     */
    public static double sub(Double value1, Double value2) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));    // 注意: 在构造BigDecimal时Double类型要先转为字符串类型
        BigDecimal b2 = new BigDecimal(Double.toString(value2));    // 注意: 在构造BigDecimal时Double类型要先转为字符串类型
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供(相对)精确的减法运算，精确到小数点以后scale位，以后的数字四舍五入。
     */
    public static double sub(Double value1, Double value2, int scale) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));    // 注意: 在构造BigDecimal时Double类型要先转为字符串类型
        BigDecimal b2 = new BigDecimal(Double.toString(value2));    // 注意: 在构造BigDecimal时Double类型要先转为字符串类型
        return b1.subtract(b2, new MathContext(scale)).doubleValue();
    }

    /**
     * 提供精确的乘法运算。
     */
    public static double mul(Double value1, Double value2) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));    // 注意: 在构造BigDecimal时Double类型要先转为字符串类型
        BigDecimal b2 = new BigDecimal(Double.toString(value2));    // 注意: 在构造BigDecimal时Double类型要先转为字符串类型
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供(相对)精确的乘法运算，精确到小数点以后scale位，以后的数字四舍五入。
     */
    public static double mul(Double value1, Double value2, int scale) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));    // 注意: 在构造BigDecimal时Double类型要先转为字符串类型
        BigDecimal b2 = new BigDecimal(Double.toString(value2));    // 注意: 在构造BigDecimal时Double类型要先转为字符串类型
        return b1.multiply(b2, new MathContext(scale)).doubleValue();
    }

    /**
     * 提供精确的除法运算
     */
    public static Double divide(Double dividend, Double divisor) {
        BigDecimal b1 = new BigDecimal(Double.toString(dividend));  // 注意: 在构造BigDecimal时Double类型要先转为字符串类型
        BigDecimal b2 = new BigDecimal(Double.toString(divisor));   // 注意: 在构造BigDecimal时Double类型要先转为字符串类型
        return b1.divide(b2).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算，精确到小数点以后scale位，以后的数字四舍五入。
     */
    public static Double divide(Double dividend, Double divisor, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(dividend));  // 注意: 在构造BigDecimal时Double类型要先转为字符串类型
        BigDecimal b2 = new BigDecimal(Double.toString(divisor));   // 注意: 在构造BigDecimal时Double类型要先转为字符串类型
        return b1.divide(b2, new MathContext(scale)).doubleValue();
    }

}
