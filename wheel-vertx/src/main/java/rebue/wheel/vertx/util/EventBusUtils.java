package rebue.wheel.vertx.util;

import com.google.common.base.CaseFormat;

public class EventBusUtils {
    public static String getAddr(final Class<?> clazz) {
        return clazz.getPackage() + "." + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, clazz.getSimpleName());
    }

}
