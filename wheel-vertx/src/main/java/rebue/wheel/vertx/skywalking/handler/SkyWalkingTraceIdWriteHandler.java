package rebue.wheel.vertx.skywalking.handler;

import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.PlatformHandler;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.skywalking.SkyWalkingUtils;

/**
 * 读取Http headers中的sw8，并解析出TraceId，然后写入路由上下文和MDC
 */
@Slf4j
public class SkyWalkingTraceIdWriteHandler implements PlatformHandler {
    @Override
    public void handle(RoutingContext routingContext) {
        try {
            MultiMap headers = routingContext.request().headers();
            String   traceId = SkyWalkingUtils.getTraceIdFromHttpHeaders(headers);
            SkyWalkingUtils.putTraceIdInMdc(traceId);
        } catch (Exception e) {
            log.error("设置trace id时异常", e);
        }
        routingContext.next();
    }

}
