package rebue.wheel.core;

import java.math.BigDecimal;

/**
 * BigDecimal工具类(主要是对BigDecimal类的使用提供示范)
 * XXX wheel : 浮点运算: 注意在构造Double类型的BigDecimal时不能直接用new BigDecimal(Double)的方法
 *
 * @author zbz
 */
public class BigDecimalUtils {
    /**
     * 判断是否相等(应该用compareTo)。
     * equals方法会比较值和精确度，而compareTo则会忽略精度。
     */
    public static boolean equals(final Double value1, final Double value2) {
        final BigDecimal b1 = BigDecimal.valueOf(value1);    // 注意: 在构造Double类型的BigDecimal时不能直接用new BigDecimal(Double)的方法
        final BigDecimal b2 = BigDecimal.valueOf(value2);    // 注意: 在构造Double类型的BigDecimal时不能直接用new BigDecimal(Double)的方法
        return b1.compareTo(b2) == 0;
    }

    /**
     * 提供精确的加法运算。
     */
    public static double add(final Double value1, final Double value2) {
        final BigDecimal b1 = BigDecimal.valueOf(value1);    // 注意: 在构造Double类型的BigDecimal时不能直接用new BigDecimal(Double)的方法
        final BigDecimal b2 = BigDecimal.valueOf(value2);    // 注意: 在构造Double类型的BigDecimal时不能直接用new BigDecimal(Double)的方法
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供(相对)精确的加法运算，精确到小数点以后scale位，以后的数字四舍五入。
     */
    public static double add(final Double value1, final Double value2, final int scale) {
        final BigDecimal b1 = BigDecimal.valueOf(value1);    // 注意: 在构造Double类型的BigDecimal时不能直接用new BigDecimal(Double)的方法
        final BigDecimal b2 = BigDecimal.valueOf(value2);    // 注意: 在构造Double类型的BigDecimal时不能直接用new BigDecimal(Double)的方法
        return b1.add(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供精确的减法运算。
     */
    public static double sub(final Double value1, final Double value2) {
        final BigDecimal b1 = BigDecimal.valueOf(value1);    // 注意: 在构造Double类型的BigDecimal时不能直接用new BigDecimal(Double)的方法
        final BigDecimal b2 = BigDecimal.valueOf(value2);    // 注意: 在构造Double类型的BigDecimal时不能直接用new BigDecimal(Double)的方法
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供(相对)精确的减法运算，精确到小数点以后scale位，以后的数字四舍五入。
     */
    public static double sub(final Double value1, final Double value2, final int scale) {
        final BigDecimal b1 = BigDecimal.valueOf(value1);    // 注意: 在构造Double类型的BigDecimal时不能直接用new BigDecimal(Double)的方法
        final BigDecimal b2 = BigDecimal.valueOf(value2);    // 注意: 在构造Double类型的BigDecimal时不能直接用new BigDecimal(Double)的方法
        return b1.subtract(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供精确的乘法运算。
     */
    public static double mul(final Double value1, final Double value2) {
        final BigDecimal b1 = BigDecimal.valueOf(value1);    // 注意: 在构造Double类型的BigDecimal时不能直接用new BigDecimal(Double)的方法
        final BigDecimal b2 = BigDecimal.valueOf(value2);    // 注意: 在构造Double类型的BigDecimal时不能直接用new BigDecimal(Double)的方法
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供(相对)精确的乘法运算，精确到小数点以后scale位，以后的数字四舍五入。
     */
    public static double mul(final Double value1, final Double value2, final int scale) {
        final BigDecimal b1 = BigDecimal.valueOf(value1);    // 注意: 在构造Double类型的BigDecimal时不能直接用new BigDecimal(Double)的方法
        final BigDecimal b2 = BigDecimal.valueOf(value2);    // 注意: 在构造Double类型的BigDecimal时不能直接用new BigDecimal(Double)的方法
        return b1.multiply(b2).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供精确的除法运算
     */
    public static Double divide(final Double dividend, final Double divisor) {
        final BigDecimal b1 = BigDecimal.valueOf(dividend);  // 注意: 在构造Double类型的BigDecimal时不能直接用new BigDecimal(Double)的方法
        final BigDecimal b2 = BigDecimal.valueOf(divisor);   // 注意: 在构造Double类型的BigDecimal时不能直接用new BigDecimal(Double)的方法
        return b1.divide(b2).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算，精确到小数点以后scale位，以后的数字四舍五入。
     */
    public static Double divide(final Double dividend, final Double divisor, final int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        final BigDecimal b1 = BigDecimal.valueOf(dividend);  // 注意: 在构造Double类型的BigDecimal时不能直接用new BigDecimal(Double)的方法
        final BigDecimal b2 = BigDecimal.valueOf(divisor);   // 注意: 在构造Double类型的BigDecimal时不能直接用new BigDecimal(Double)的方法
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}