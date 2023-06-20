package rebue.wheel.vertx.skywalking.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.skywalking.SkyWalkingUtils;

/**
 * 从路由上下文中读取TraceId，然后放入MDC中
 */
@Slf4j
@Deprecated
public class SkyWalkingTraceIdReadHandler implements Handler<RoutingContext> {
    private final Handler<RoutingContext> innerHandler;

    public SkyWalkingTraceIdReadHandler(Handler<RoutingContext> innerHandler) {
        this.innerHandler = innerHandler;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        try {
//            log.debug("TraceContext.trace id: {}", TraceContext.traceId());
            String traceId = routingContext.get(SkyWalkingUtils.TRACE_ID_KEY);
            SkyWalkingUtils.putTraceIdInMdc(traceId);
        } catch (Exception e) {
            log.error("读取trace id时异常", e);
        }
        innerHandler.handle(routingContext);
        SkyWalkingUtils.clearTraceIdInMdc();
    }

}
