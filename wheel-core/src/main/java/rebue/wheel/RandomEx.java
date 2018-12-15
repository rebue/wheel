package rebue.wheel;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rebue.wheel.exception.RuntimeExceptionX;

/**
 * @since 1.7
 */
public class RandomEx {
    private static final Logger _log    = LoggerFactory.getLogger(RandomEx.class);

    private static String       factor1 = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String       factor2 = "1234567890";
    private static SecureRandom random;

    static {
        try {
            random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    public static Random getRandom() {
        return random;
    }

    /**
     * 随机生成UUID（不含破折号）
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    /**
     * 生成resultSize位的随机数(只包含数字和大小写的字母)
     */
    public static String random1(final int resultSize) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < resultSize; i++) {
            stringBuilder.append(factor1.charAt(random.nextInt(factor1.length())));
        }
        return stringBuilder.toString();
    }

    /**
     * 生成resultSize位的随机数(只包含数字)
     */
    public static String random2(final int resultSize) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < resultSize; i++) {
            stringBuilder.append(factor2.charAt(random.nextInt(factor2.length())));
        }
        return stringBuilder.toString();
    }

    /**
     * 生成resultSize位的随机汉字
     */
    public static String randomCn(final int resultSize) {
        _log.info("生成{}位的随机汉字", resultSize);
        int hightPos, lowPos; // 定义高低位
        hightPos = (176 + Math.abs(random.nextInt(39)));// 获取高位值
        lowPos = (161 + Math.abs(random.nextInt(93)));// 获取低位值
        final byte[] b = new byte[2];
        b[0] = (new Integer(hightPos).byteValue());
        b[1] = (new Integer(lowPos).byteValue());
        String str;
        try {
            str = new String(b, "utf-8");   // 转成中文

            return str;
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeExceptionX("不支持的编码", e);
        }
    }

    /**
     * 生成一个属性值随机的对象
     */
    public static Object randomPojo(final Class<?> clazz) {
        _log.info("创建一个属性值随机的对象");
        try {
            final Object model = clazz.newInstance();

            // 获取实体类的所有属性，返回Field数组
            final Field[] field = clazz.getDeclaredFields();
            // 获取属性的名字
            for (final Field element : field) {
                // 获取属性的名字
                String name = element.getName();
                // 获取属性类型
                final String type = element.getGenericType().toString();
                // 关键。。。可访问私有变量
                element.setAccessible(true);
                // 将属性的首字母大写
                name = name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
                if (type.equals("class java.lang.String")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, String.class);
                    m.invoke(model, "asdasad");
                } else if (type.equals("class java.lang.Long")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, Long.class);
                    m.invoke(model, random.nextLong());
                } else if (type.equals("class java.lang.Integer")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, Integer.class);
                    m.invoke(model, random.nextInt());
                } else if (type.equals("class java.lang.Short")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, Short.class);
                    m.invoke(model, (short) 1);
                } else if (type.equals("class java.lang.Byte")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, Byte.class);
                    m.invoke(model, (byte) 1);
                } else if (type.equals("class java.lang.Double")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, Double.class);
                    m.invoke(model, random.nextDouble());
                } else if (type.equals("class java.math.BigDecimal")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, BigDecimal.class);
                    m.invoke(model, BigDecimal.valueOf(random.nextDouble()));
                } else if (type.equals("class java.lang.Boolean")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, Boolean.class);
                    m.invoke(model, random.nextBoolean());
                } else if (type.equals("class java.util.Date")) {
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    final Method m = clazz.getMethod("set" + name, Date.class);
                    m.invoke(model, new Date());
                }
            }
            return model;
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeExceptionX("", e);
        }
    }
}
