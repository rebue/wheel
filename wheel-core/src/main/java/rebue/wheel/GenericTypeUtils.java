package rebue.wheel;

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
     * @param classInstance
     *            承载泛型的类的实例
     */
    public static Class<?> getClassOfGeneric(final Object classInstance) {
        return getClassOfGeneric(classInstance, 1);
    }

    /**
     * 获取承载泛型的类的第n个泛型(从1数起)
     * 
     * @param classInstance
     *            承载泛型的类的实例
     * @param n
     *            第n个泛型(从0数起)
     * @return
     */
    public static Class<?> getClassOfGeneric(final Object classInstance, final int n) {
        return (Class<?>) ((ParameterizedType) classInstance.getClass().getGenericSuperclass()).getActualTypeArguments()[n - 1];
    }
}
