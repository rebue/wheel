/**
 * XXX 复制io.vertx.ext.web.proxy.handler.impl.ProxyHandlerImpl类的代码
 * 1. body只能读取一次，如果想解析body，代理就无法再次读取并转发给目标服务器
 * 所以需要先把body缓存起来，再将缓存中的body用新的请求包装类包装起来，让代理可以再次从请求中读取
 * 2. 在响应前添加拦截器，并且支持proxyContext参数，使其能在一次代理会话中传递参数
 * 这样就可以将之前请求的body传到拦截器中进行处理
 */
package rebue.wheel.vertx.web.proxy.handler.impl;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.AllowForwardHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.proxy.handler.ProxyHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import rebue.wheel.vertx.httpproxy.HttpProxyEx;
import rebue.wheel.vertx.httpproxy.ProxyInterceptorEx;
import rebue.wheel.vertx.web.impl.HttpServerRequestWrapperEx;

import java.util.List;

/**
 * @author <a href="mailto:emad.albloushi@gmail.com">Emad Alblueshi</a>
 */
@Slf4j
public class ProxyHandlerImplEx implements ProxyHandler {

    private final HttpProxyEx httpProxy;

    public ProxyHandlerImplEx(HttpProxyEx httpProxy) {
        this.httpProxy = httpProxy;
    }

    public ProxyHandlerImplEx(HttpProxyEx httpProxy, int port, String host) {
        this.httpProxy = httpProxy.origin(port, host);
    }

    /**
     * XXX 1. 如果有缓存body，包装新的请求类
     * XXX 2. 在响应前添加拦截器
     */
    @Override
    public void handle(RoutingContext routingContext) {
        log.debug("ProxyHandler.handleEx");
        HttpServerRequest request = routingContext.request();
        log.debug("request: {}:{}{}", request.method(), request.host(), request.uri());
        final String body = routingContext.get("body");
        // body只能读取一次，如果之前读取到了缓存中，将其放入请求的包装类中，让其可以再次从请求中读取
        if (StringUtils.isNotBlank(body)) {
            log.debug("body: {}", body);
            request = new HttpServerRequestWrapperEx(request, AllowForwardHeaders.NONE);
            ((HttpServerRequestWrapperEx) request).changBodyTo(body);
        }

        // 在响应前添加拦截器，并且支持proxyContext参数，使其能在一次代理会话中传递参数
        httpProxy.handleEx(request, proxyContext -> {
            log.debug("ProxyHandler.handleEx.compose");
            log.debug("foreach proxyInterceptor beforeResponse");
            final List<ProxyInterceptorEx> filters = httpProxy.getInterceptors();
            for (int i = filters.size() - 1; i >= 0; i--) {
                final ProxyInterceptorEx filter = filters.get(i);
                if (!filter.beforeResponse(routingContext, proxyContext)) {
                    return;
                }
            }
        });

    }

}
