/**
 * XXX 复制io.vertx.ext.web.proxy.handler.impl.ProxyHandlerImpl类的代码，原类会让ctx的后置处理器失效
 */
package rebue.wheel.vertx.web.proxy.handler.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.AllowForwardHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.proxy.handler.ProxyHandler;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.httpproxy.HttpProxyEx;
import rebue.wheel.vertx.httpproxy.ProxyInterceptorEx;
import rebue.wheel.vertx.web.impl.HttpServerRequestWrapperEx;

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
     * XXX 在代理完成响应时，调用ctx.next()方法传递给下一个处理器，解决后置过滤器失效的问题
     */
    @Override
    public void handle(RoutingContext routingContext) {
        log.debug("ProxyHandler.handleEx");
        final String body = routingContext.get("body");
        log.debug("body: {}", body);
        HttpServerRequest request = routingContext.request();
        if (StringUtils.isNotBlank(body)) {
            request = new HttpServerRequestWrapperEx(request, AllowForwardHeaders.NONE);
            ((HttpServerRequestWrapperEx) request).changTo(Buffer.buffer(body));
        }

        httpProxy.handleEx(request).compose(proxyContext -> {
            log.debug("ProxyHandler.handleEx.compose");
            final List<ProxyInterceptorEx> filters = httpProxy.getInterceptors();
            for (int i = filters.size() - 1; i >= 0; i--) {
                final ProxyInterceptorEx filter = filters.get(i);
                if (!filter.beforeEndReponse(routingContext, proxyContext)) {
                    break;
                }
            }

            // routingContext.next();
            return Future.succeededFuture();
        });
    }
}
