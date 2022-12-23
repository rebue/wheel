/**
 * XXX 复制4.3.7版本的io.vertx.httpproxy.HttpProxy接口的代码，原接口会让ctx的后置处理器失效
 * Copyright (c) 2011-2020 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package rebue.wheel.vertx.httpproxy;

import java.util.List;
import java.util.function.Function;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.net.SocketAddress;
import io.vertx.httpproxy.ProxyContext;
import io.vertx.httpproxy.ProxyOptions;
import rebue.wheel.vertx.httpproxy.impl.ReverseProxyEx;

import java.util.function.BiFunction;

/**
 * Handles the HTTP reverse proxy logic between the <i><b>user agent</b></i> and the <i><b>origin</b></i>.
 * <p>
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@VertxGen
public interface HttpProxyEx extends Handler<HttpServerRequest> {

    /**
     * Create a new {@code HttpProxyEx} instance.
     *
     * @param client the {@code HttpClient} that forwards <i><b>outbound</b></i> requests to the <i><b>origin</b></i>.
     * @return a reference to this, so the API can be used fluently.
     */
    static HttpProxyEx reverseProxy(HttpClient client) {
        return new ReverseProxyEx(new ProxyOptions(), client);
    }

    /**
     * Create a new {@code HttpProxyEx} instance.
     *
     * @param client the {@code HttpClient} that forwards <i><b>outbound</b></i> requests to the <i><b>origin</b></i>.
     * @return a reference to this, so the API can be used fluently.
     */
    static HttpProxyEx reverseProxy(ProxyOptions options, HttpClient client) {
        return new ReverseProxyEx(options, client);
    }

    /**
     * Set the {@code SocketAddress} of the <i><b>origin</b></i>.
     *
     * @param address the {@code SocketAddress} of the <i><b>origin</b></i>
     * @return a reference to this, so the API can be used fluently
     */
    @Fluent
    default HttpProxyEx origin(SocketAddress address) {
        return originSelector(req -> Future.succeededFuture(address));
    }

    /**
     * Set the host name and port number of the <i><b>origin</b></i>.
     *
     * @param port the port number of the <i><b>origin</b></i> server
     * @param host the host name of the <i><b>origin</b></i> server
     * @return a reference to this, so the API can be used fluently
     */
    @Fluent
    default HttpProxyEx origin(int port, String host) {
        return origin(SocketAddress.inetSocketAddress(port, host));
    }

    /**
     * Set a selector that resolves the <i><b>origin</b></i> address based on the incoming HTTP request.
     *
     * @param selector the selector
     * @return a reference to this, so the API can be used fluently
     */
    @Fluent
    default HttpProxyEx originSelector(Function<HttpServerRequest, Future<SocketAddress>> selector) {
        return originRequestProvider((req, client) -> selector
                .apply(req)
                .flatMap(server -> client.request(new RequestOptions().setServer(server))));
    }

    /**
     * Set a provider that creates the request to the <i><b>origin</b></i> server based the incoming HTTP request.
     * Setting a provider overrides any origin selector previously set.
     *
     * @param provider the provider
     * @return a reference to this, so the API can be used fluently
     */
    @GenIgnore()
    @Fluent
    HttpProxyEx originRequestProvider(BiFunction<HttpServerRequest, HttpClient, Future<HttpClientRequest>> provider);

    /**
     * Add an interceptor to the interceptor chain.
     *
     * @param interceptor
     * @return a reference to this, so the API can be used fluently
     */
    @Fluent
    HttpProxyEx addInterceptor(ProxyInterceptorEx interceptor);

    /**
     * Handle the <i><b>outbound</b></i> {@code HttpServerRequest}.
     *
     * @param request the outbound {@code HttpServerRequest}
     */
    void handle(HttpServerRequest request);

    /**
     * XXX 添加能返回future的处理方法
     *
     * @param request 请求
     *
     */
    Future<ProxyContext> handleEx(HttpServerRequest request);

    /**
     * XXX 获取拦截器
     */
    List<ProxyInterceptorEx> getInterceptors();

}
