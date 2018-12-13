package rebue.wheel.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Date;

/**
 * 随机生成实体类数据
 * 
 * @author lbl
 *
 */
public class MockDataUtils {
    private static SecureRandom random;

    static {
        try {
            random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建新的、属性值随机的对象
     * 
     * @param model
     *            对象
     */
    public static Object newRandomPojo(final Class<?> clazz) throws ReflectiveOperationException {
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
    }
}
