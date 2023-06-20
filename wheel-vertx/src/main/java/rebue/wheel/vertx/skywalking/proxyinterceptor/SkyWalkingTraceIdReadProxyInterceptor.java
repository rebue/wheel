package rebue.wheel.vertx.skywalking.proxyinterceptor;

import io.vertx.core.Future;
import io.vertx.httpproxy.ProxyContext;
import io.vertx.httpproxy.ProxyInterceptor;
import io.vertx.httpproxy.ProxyResponse;
import rebue.wheel.vertx.skywalking.SkyWalkingUtils;

@Deprecated
public class SkyWalkingTraceIdReadProxyInterceptor implements ProxyInterceptor {
    private final ProxyInterceptor innerProxyInterceptor;

    public SkyWalkingTraceIdReadProxyInterceptor(ProxyInterceptor innerProxyInterceptor) {
        this.innerProxyInterceptor = innerProxyInterceptor;
    }

    @Override
    public Future<ProxyResponse> handleProxyRequest(ProxyContext context) {
        String traceId = context.get(SkyWalkingUtils.TRACE_ID_KEY, String.class);
        SkyWalkingUtils.putTraceIdInMdc(traceId);
        Future<ProxyResponse> result = innerProxyInterceptor.handleProxyRequest(context);
        SkyWalkingUtils.clearTraceIdInMdc();
        return result;
    }

    @Override
    public Future<Void> handleProxyResponse(ProxyContext context) {
        String traceId = context.get(SkyWalkingUtils.TRACE_ID_KEY, String.class);
        SkyWalkingUtils.putTraceIdInMdc(traceId);
        Future<Void> result = innerProxyInterceptor.handleProxyResponse(context);
        SkyWalkingUtils.clearTraceIdInMdc();
        return result;
    }

}
