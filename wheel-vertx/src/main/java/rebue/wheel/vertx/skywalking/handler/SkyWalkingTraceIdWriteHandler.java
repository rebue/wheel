package rebue.wheel.vertx.skywalking.handler;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import rebue.wheel.vertx.skywalking.SkyWalkingUtils;

/**
 * 读取Http headers中的sw8，并解析出TraceId，然后写入路由上下文和MDC
 */
@Slf4j
public class SkyWalkingTraceIdWriteHandler implements Handler<RoutingContext> {
    @Override
    public void handle(RoutingContext routingContext) {
        try {
            log.debug("TraceContext.trace id: {}", TraceContext.traceId());
            MultiMap headers = routingContext.request().headers();
            String   traceId = SkyWalkingUtils.getTraceIdFromHttpHeaders(headers);
            SkyWalkingUtils.putTraceIdInMdc(traceId);
            routingContext.put(SkyWalkingUtils.TRACE_ID_KEY, traceId);
        } catch (Exception e) {
            log.error("设置trace id时异常", e);
        }
        SkyWalkingUtils.clearTraceIdInMdc();
        routingContext.next();
    }

}
