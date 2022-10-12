/**
 * XXX 复制io.vertx.httpproxy.impl.ReverseProxy类的代码，原类会让ctx的后置处理器失效
 * Copyright (c) 2011-2020 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package rebue.wheel.vertx.httpproxy.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Function;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import io.vertx.httpproxy.ProxyContext;
import io.vertx.httpproxy.ProxyOptions;
import io.vertx.httpproxy.ProxyRequest;
import io.vertx.httpproxy.ProxyResponse;
import io.vertx.httpproxy.cache.CacheOptions;
import io.vertx.httpproxy.spi.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.httpproxy.HttpProxyEx;
import rebue.wheel.vertx.httpproxy.ProxyInterceptorEx;

@Slf4j
public class ReverseProxyEx implements HttpProxyEx {

    private final HttpClient                                   client;
    private final boolean                                      supportWebSocket;
    private Function<HttpServerRequest, Future<SocketAddress>> selector     = req -> Future.failedFuture("No origin available");
    private final List<ProxyInterceptorEx>                     interceptors = new ArrayList<>();

    public ReverseProxyEx(ProxyOptions options, HttpClient client) {
        final CacheOptions cacheOptions = options.getCacheOptions();
        if (cacheOptions != null) {
            final Cache<String, ResourceEx> cache = cacheOptions.newCache();
            addInterceptor(new CachingFilterEx(cache));
        }
        this.client           = client;
        this.supportWebSocket = options.getSupportWebSocket();
    }

    @Override
    public HttpProxyEx originSelector(Function<HttpServerRequest, Future<SocketAddress>> selector) {
        this.selector = selector;
        return this;
    }

    @Override
    public HttpProxyEx addInterceptor(ProxyInterceptorEx interceptor) {
        interceptors.add(interceptor);
        return this;
    }

    @Override
    public void handle(HttpServerRequest request) {
        final ProxyRequest proxyRequest = ProxyRequest.reverseProxy(request);

        // Encoding sanity check
        final Boolean chunked = HttpUtilsEx.isChunked(request.headers());
        if (chunked == null) {
            end(proxyRequest, 400);
            return;
        }

        // WebSocket upgrade tunneling
        if (supportWebSocket &&
                request.version() == HttpVersion.HTTP_1_1 &&
                request.method() == HttpMethod.GET &&
                request.headers().contains(HttpHeaders.CONNECTION, HttpHeaders.UPGRADE, true)) {
            handleWebSocketUpgrade(proxyRequest);
            return;
        }

        final Proxy proxy = new Proxy(proxyRequest);
        proxy.filters = interceptors.listIterator();
        proxy.sendRequest().compose(proxy::sendProxyResponse);
    }

    /**
     * XXX 返回future
     */
    @Override
    public Future<ProxyContext> handleEx(HttpServerRequest request) {
        final ProxyRequest proxyRequest = ProxyRequest.reverseProxy(request);

        // Encoding sanity check
        final Boolean chunked = HttpUtilsEx.isChunked(request.headers());
        if (chunked == null) {
            end(proxyRequest, 400);
            return Future.succeededFuture();
        }

        // WebSocket upgrade tunneling
        if (supportWebSocket &&
                request.version() == HttpVersion.HTTP_1_1 &&
                request.method() == HttpMethod.GET &&
                request.headers().contains(HttpHeaders.CONNECTION, HttpHeaders.UPGRADE, true)) {
            handleWebSocketUpgrade(proxyRequest);
            return Future.succeededFuture();
        }

        final Proxy proxy = new Proxy(proxyRequest);
        proxy.filters = interceptors.listIterator();
        return proxy.sendRequest().compose(proxy::sendProxyResponse).compose(v -> Future.succeededFuture(proxy));
    }

    private void handleWebSocketUpgrade(ProxyRequest proxyRequest) {
        final HttpServerRequest proxiedRequest = proxyRequest.proxiedRequest();
        resolveOrigin(proxiedRequest).onComplete(ar -> {
            if (ar.succeeded()) {
                final HttpClientRequest request = ar.result();
                request.setMethod(HttpMethod.GET);
                request.setURI(proxiedRequest.uri());
                request.headers().addAll(proxiedRequest.headers());
                final Future<HttpClientResponse> fut2 = request.connect();
                proxiedRequest.handler(request::write);
                proxiedRequest.endHandler(v -> request.end());
                proxiedRequest.resume();
                fut2.onComplete(ar2 -> {
                    if (ar2.succeeded()) {
                        final HttpClientResponse proxiedResponse = ar2.result();
                        if (proxiedResponse.statusCode() == 101) {
                            final HttpServerResponse response = proxiedRequest.response();
                            response.setStatusCode(101);
                            response.headers().addAll(proxiedResponse.headers());
                            final Future<NetSocket> otherso = proxiedRequest.toNetSocket();
                            otherso.onComplete(ar3 -> {
                                if (ar3.succeeded()) {
                                    final NetSocket responseSocket      = ar3.result();
                                    final NetSocket proxyResponseSocket = proxiedResponse.netSocket();
                                    responseSocket.handler(proxyResponseSocket::write);
                                    proxyResponseSocket.handler(responseSocket::write);
                                    responseSocket.closeHandler(v -> proxyResponseSocket.close());
                                    proxyResponseSocket.closeHandler(v -> responseSocket.close());
                                } else {
                                    // Find reproducer
                                    System.err.println("Handle this case");
                                    ar3.cause().printStackTrace();
                                }
                            });
                        } else {
                            // Rejection
                            proxiedRequest.resume();
                            end(proxyRequest, proxiedResponse.statusCode());
                        }
                    } else {
                        proxiedRequest.resume();
                        end(proxyRequest, 502);
                    }
                });
            } else {
                proxiedRequest.resume();
                end(proxyRequest, 502);
            }
        });
    }

    private void end(ProxyRequest proxyRequest, int sc) {
        proxyRequest
                .response()
                .release()
                .setStatusCode(sc)
                .putHeader(HttpHeaders.CONTENT_LENGTH, "0")
                .setBody(null)
                .send();
    }

    private Future<HttpClientRequest> resolveOrigin(HttpServerRequest proxiedRequest) {
        return selector.apply(proxiedRequest).flatMap(server -> {
            // XXX 打印请求服务器地址
            log.debug("request -> server {}:{}", server.host(), server.port());

            final RequestOptions requestOptions = new RequestOptions();
            requestOptions.setServer(server);
            return client.request(requestOptions);
        });
    }

    private class Proxy implements ProxyContext {

        private final ProxyRequest               request;
        private ProxyResponse                    response;
        private final Map<String, Object>        attachments = new HashMap<>();
        private ListIterator<ProxyInterceptorEx> filters;

        private Proxy(ProxyRequest request) {
            this.request = request;
        }

        @Override
        public void set(String name, Object value) {
            attachments.put(name, value);
        }

        @Override
        public <T> T get(String name, Class<T> type) {
            final Object o = attachments.get(name);
            return type.isInstance(o) ? type.cast(o) : null;
        }

        @Override
        public ProxyRequest request() {
            return request;
        }

        @Override
        public Future<ProxyResponse> sendRequest() {
            if (filters.hasNext()) {
                final ProxyInterceptorEx next = filters.next();
                return next.handleProxyRequest(this);
            }
            return sendProxyRequest(request);
        }

        @Override
        public ProxyResponse response() {
            return response;
        }

        @Override
        public Future<Void> sendResponse() {
            if (filters.hasPrevious()) {
                final ProxyInterceptorEx filter = filters.previous();
                return filter.handleProxyResponse(this);
            }
            return response.send();
        }

        private Future<ProxyResponse> sendProxyRequest(ProxyRequest proxyRequest) {
            final Future<HttpClientRequest> f = resolveOrigin(proxyRequest.proxiedRequest());
            f.onFailure(err -> {
                // Should this be done here ? I don't think so
                final HttpServerRequest proxiedRequest = proxyRequest.proxiedRequest();
                proxiedRequest.resume();
                final Promise<Void> promise = Promise.promise();
                proxiedRequest.exceptionHandler(promise::tryFail);
                proxiedRequest.endHandler(promise::tryComplete);
                promise.future().onComplete(ar2 -> {
                    end(proxyRequest, 502);
                });
            });
            return f.compose(a -> sendProxyRequest(proxyRequest, a));
        }

        private Future<ProxyResponse> sendProxyRequest(ProxyRequest proxyRequest, HttpClientRequest request) {
            log.debug("ReverseProxyEx.sendProxyRequest");
            final Future<ProxyResponse> fut = proxyRequest.send(request);
            fut.onFailure(err -> {
                proxyRequest.proxiedRequest().response().setStatusCode(502).end();
            });
            return fut;
        }

        private Future<Void> sendProxyResponse(ProxyResponse response) {

            this.response = response;

            // Check validity
            final Boolean chunked = HttpUtilsEx.isChunked(response.headers());
            if (chunked == null) {
                // response.request().release(); // Is it needed ???
                end(response.request(), 501);
                return Future.succeededFuture(); // should use END future here ???
            }

            return sendResponse();
        }
    }

    /**
     * XXX 获取拦截器
     */
    @Override
    public List<ProxyInterceptorEx> getInterceptors() {
        return interceptors;
    }

    /**
     * XXX 获取客户端
     */
    @Override
    public HttpClient getClient() {
        return client;
    }
}
