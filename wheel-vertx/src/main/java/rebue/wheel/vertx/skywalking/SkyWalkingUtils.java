package rebue.wheel.vertx.skywalking;

import io.vertx.core.MultiMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;

import java.util.Base64;

@Slf4j
@Deprecated
public class SkyWalkingUtils {
    public final static String TRACE_ID_KEY = "traceId";

    public static boolean isEnabled() {
        String sw_agent_enable = System.getenv("SW_AGENT_ENABLE");
        return sw_agent_enable != null && sw_agent_enable.equalsIgnoreCase("true");
    }

    public static String getTraceIdFromHttpHeaders(MultiMap headers) {
        log.debug("headers: {}", headers);
        String sw8 = headers.get("sw8");
        if (StringUtils.isBlank(sw8)) {
            return "N/A";
        }
        String result = new String(Base64.getDecoder().decode(sw8.substring(2, sw8.indexOf('-', 2))));
        log.debug("gat trace id from HttpHeaders: {}", result);
        return result;
    }

    public static void putTraceIdInMdc(String traceId) {
        log.debug("TraceContext.traceId(): {}", TraceContext.traceId());
        log.debug("TraceContext.segmentId(): {}", TraceContext.segmentId());
        log.debug("TraceContext.spanId(): {}", TraceContext.spanId());
        String TID = "TID:%s".formatted(traceId);
        MDC.put(SkyWalkingUtils.TRACE_ID_KEY, TID);
//        MDC.put("tid", TID);
        log.debug("put trace id in MDC: {}", TID);
    }

    public static void clearTraceIdInMdc() {
        MDC.put(SkyWalkingUtils.TRACE_ID_KEY, "N/A");
        MDC.put("tid", "N/A");
    }
}
