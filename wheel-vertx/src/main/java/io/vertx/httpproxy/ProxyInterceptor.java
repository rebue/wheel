/**
 * XXX 复制4.4.3版本的io.vertx.httpproxy.impl.ProxyInterceptor接口的代码，让websocket也支持代理拦截器
 * 扩展ProxyInterceptor接口，添加修改代理请求的方法
 */
package io.vertx.httpproxy;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;

/**
 * A {@link HttpProxy} interceptor.
 */
@VertxGen(concrete = false)
public interface ProxyInterceptor {

    /**
     * XXX 默认调用modifyProxyRequest方法
     *
     * Handle the proxy request at the stage of this interceptor.
     *
     * @param context the proxy context
     * @return when the request has actually been sent to the origin
     */
    default Future<ProxyResponse> handleProxyRequest(ProxyContext context) {
        // XXX 默认调用modifyProxyRequest方法
        this.modifyProxyRequest(context.request());
        // 继续拦截器
        return context.sendRequest();
    }

    /**
     * Handle the proxy response at the stage of this interceptor.
     *
     * @param context the proxy context
     * @return when the response has actually been sent to the user-agent
     */
    default Future<Void> handleProxyResponse(ProxyContext context) {
        return context.sendResponse();
    }

    /**
     * XXX 修改代理请求
     *
     * @param proxyRequest 要修改的代理请求
     */
    default void modifyProxyRequest(ProxyRequest proxyRequest) {
    }

}
