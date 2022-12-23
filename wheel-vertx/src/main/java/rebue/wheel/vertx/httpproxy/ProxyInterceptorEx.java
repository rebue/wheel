/**
 * 扩展ProxyInterceptor接口
 * 添加在代理响应前可以
 */
package rebue.wheel.vertx.httpproxy;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.ext.web.RoutingContext;
import io.vertx.httpproxy.ProxyContext;
import io.vertx.httpproxy.ProxyInterceptor;

@VertxGen
public interface ProxyInterceptorEx extends ProxyInterceptor {

    /**
     * 结束响应之前
     */
    default boolean beforeEndResponse(RoutingContext routingContext, ProxyContext proxyContext) {
        return true;
    }

}
