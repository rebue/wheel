package rebue.wheel.vertx.skywalking.proxyinterceptor;

import io.vertx.core.Future;
import io.vertx.httpproxy.ProxyContext;
import io.vertx.httpproxy.ProxyInterceptor;
import io.vertx.httpproxy.ProxyResponse;
import rebue.wheel.vertx.skywalking.SkyWalkingUtils;

public class SkyWalkingTraceIdWriteProxyInterceptor implements ProxyInterceptor {

    @Override
    public Future<ProxyResponse> handleProxyRequest(ProxyContext context) {
        String traceId = SkyWalkingUtils.getTraceIdFromHttpHeaders(context.request().headers());
        SkyWalkingUtils.putTraceIdInMdc(traceId);
        context.set(SkyWalkingUtils.TRACE_ID_KEY, traceId);
        SkyWalkingUtils.clearTraceIdInMdc();
        // 继续拦截器
        return context.sendRequest();
    }
}
