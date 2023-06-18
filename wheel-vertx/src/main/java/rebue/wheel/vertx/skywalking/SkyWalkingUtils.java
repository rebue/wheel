package rebue.wheel.vertx.skywalking;

import io.vertx.core.MultiMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.Base64;

@Slf4j
public class SkyWalkingUtils {
    public final static String TRACE_ID_KEY = "traceId";

    public static boolean isEnabled() {
        return System.getenv("SW_AGENT_ENABLE").equalsIgnoreCase("true");
    }

    public static String getTraceIdFromHttpHeaders(MultiMap headers) {
        log.debug("headers: {}", headers);
        String sw8 = headers.get("sw8");
        if (StringUtils.isBlank(sw8)) {
            return "N/A";
        }
        return new String(Base64.getDecoder().decode(sw8.substring(2, sw8.indexOf('-', 2))));
    }

    public static void putTraceIdInMdc(String traceId) {
        String TID = "TID:%s".formatted(traceId);
        MDC.put(SkyWalkingUtils.TRACE_ID_KEY, TID);
        MDC.put("tid", TID);
        log.debug("put trace id in MDC: {}", TID);
    }

    public static void clearTraceIdInMdc() {
        MDC.put(SkyWalkingUtils.TRACE_ID_KEY, "N/A");
        MDC.put("tid", "N/A");
    }
}
