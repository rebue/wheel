package rebue.wheel.core;

public class ClassUtils {
    /**
     * 从类全名中获取类简称
     * 
     * @param className 类全名
     * @return 类简称
     */
    public static String getClassSimpleName(String className) {
        return className.substring(className.lastIndexOf('.') + 1);
    }
}
