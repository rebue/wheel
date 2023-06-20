package rebue.wheel.vertx.skywalking.proxyinterceptor;

import io.vertx.core.Future;
import io.vertx.httpproxy.ProxyContext;
import io.vertx.httpproxy.ProxyInterceptor;
import io.vertx.httpproxy.ProxyResponse;
import rebue.wheel.vertx.skywalking.SkyWalkingUtils;

@Deprecated
public class SkyWalkingTraceIdWriteProxyInterceptor implements ProxyInterceptor {

    @Override
    public Future<ProxyResponse> handleProxyRequest(ProxyContext proxyContext) {
        String traceId = SkyWalkingUtils.getTraceIdFromHttpHeaders(proxyContext.request().headers());
        SkyWalkingUtils.putTraceIdInMdc(traceId);
//        proxyContext.set(SkyWalkingUtils.TRACE_ID_KEY, traceId);
//        SkyWalkingUtils.clearTraceIdInMdc();
        // 继续拦截器
        return proxyContext.sendRequest();
    }
}
