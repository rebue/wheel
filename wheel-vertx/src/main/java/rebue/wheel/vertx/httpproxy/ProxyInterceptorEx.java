package rebue.wheel.vertx.httpproxy;

import io.vertx.ext.web.RoutingContext;
import io.vertx.httpproxy.ProxyContext;
import io.vertx.httpproxy.ProxyInterceptor;

public interface ProxyInterceptorEx extends ProxyInterceptor {

    /**
     * 结束响应之前
     */
    default boolean beforeEndReponse(RoutingContext routingContext, ProxyContext proxyContext) {
        return true;
    }

}
