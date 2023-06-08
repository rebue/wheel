package rebue.wheel.vertx.util;

import com.xxl.job.core.context.XxlJobHelper;
import org.slf4j.Logger;

public class XxlJobUtils {
    public static void logInfo(Logger log, String msg, Object... args) {
        log.info(msg, args);
        XxlJobHelper.log(msg, args);
    }

}
