package rebue.wheel.core.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public class ApplicationContextUtils {
    private static ApplicationContext _applicationContext = null;

    public static void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        if (_applicationContext == null) {
            _applicationContext = applicationContext;
        }
    }
    // 获取applicationContext

    public static ApplicationContext getApplicationContext() {
        return _applicationContext;
    }
    // 通过name获取 Bean.

    public static Object getBean(final String name) {

        return getApplicationContext().getBean(name);

    }

    // 通过class获取Bean.
    public static <T> T getBean(final Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    // 通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(final String name, final Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}