/**
 * 扩展ProxyInterceptor接口
 * 添加在代理响应前可以
 */
package rebue.wheel.vertx.httpproxy;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.ext.web.RoutingContext;
import io.vertx.httpproxy.ProxyContext;
import io.vertx.httpproxy.ProxyInterceptor;
import io.vertx.httpproxy.ProxyRequest;

@VertxGen
@Deprecated
public interface ProxyInterceptorEx extends ProxyInterceptor {
    /**
     * 修改WebSocket的代理请求
     *
     * @param proxyRequest 要修改的代理请求
     */
    default void modifyProxyRequest(ProxyRequest proxyRequest) {
    }

    /**
     * 响应前的事件
     *
     * @param routingContext 路由上下文
     * @param proxyContext   代理上下文
     * @return 是否继续下一个代理拦截器
     */
    default boolean beforeResponse(RoutingContext routingContext, ProxyContext proxyContext) {
        return true;
    }

}
