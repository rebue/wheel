package rebue.wheel.vertx.util;

import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventBusUtils {
    public static String getAddr(final Class<?> clazz) {
        final String addr = clazz.getPackage().getName() + "." + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, clazz.getSimpleName());
        log.info("获取EventBus的地址是: {}", addr);
        return addr;
    }

}
