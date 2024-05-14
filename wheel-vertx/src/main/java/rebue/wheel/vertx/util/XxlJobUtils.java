package rebue.wheel.vertx.util;

import org.slf4j.Logger;

import com.xxl.job.core.context.XxlJobHelper;

public class XxlJobUtils {
    public static void logInfo(Logger log, String msg, Object... args) {
        log.info(msg, args);
        XxlJobHelper.log(msg, args);
    }

}
