package rebue.wheel.core;

import java.lang.reflect.ParameterizedType;

/**
 * 泛型的工具类
 * 
 * @author zbz
 *
 */
public class GenericTypeUtils {

    /**
     * 获取承载泛型的类的第一个泛型
     * 
     * @param classInstance 承载泛型的类的实例
     */
    public static Class<?> getGenericClass(final Object classInstance) {
        return getGenericClass(classInstance, 0);
    }

    /**
     * 获取承载泛型的类的第n个泛型(从0数起)
     * 
     * @param classInstance 承载泛型的类的实例
     * @param index         第n个泛型(从0数起)
     */
    public static Class<?> getGenericClass(final Object classInstance, final int index) {
        return (Class<?>) ((ParameterizedType) classInstance.getClass().getGenericSuperclass()).getActualTypeArguments()[index];
    }
}
