/**
 * XXX 复制4.3.7版本的io.vertx.httpproxy.impl.ReverseProxy类的代码，原类会让ctx的后置处理器失效
 * <p>
 * Copyright (c) 2011-2020 Contributors to the Eclipse Foundation
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package rebue.wheel.vertx.httpproxy.impl;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.*;
import io.vertx.core.net.NetSocket;
import io.vertx.httpproxy.ProxyContext;
import io.vertx.httpproxy.ProxyOptions;
import io.vertx.httpproxy.ProxyRequest;
import io.vertx.httpproxy.ProxyResponse;
import io.vertx.httpproxy.cache.CacheOptions;
import io.vertx.httpproxy.spi.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.httpproxy.HttpProxyEx;
import rebue.wheel.vertx.httpproxy.ProxyInterceptorEx;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Slf4j
public class ReverseProxyEx implements HttpProxyEx {

    private final HttpClient                                                           client;
    private final boolean                                                              supportWebSocket;
    private       BiFunction<HttpServerRequest, HttpClient, Future<HttpClientRequest>> selector     = (req, client) -> Future.failedFuture("No origin available");
    private final List<ProxyInterceptorEx>                                             interceptors = new ArrayList<>();

    public ReverseProxyEx(ProxyOptions options, HttpClient client) {
        CacheOptions cacheOptions = options.getCacheOptions();
        if (cacheOptions != null) {
            Cache<String, Resource> cache = cacheOptions.newCache();
            addInterceptor(new CachingFilter(cache));
        }
        this.client = client;
        this.supportWebSocket = options.getSupportWebSocket();
    }

    @Override
    public HttpProxyEx originRequestProvider(BiFunction<HttpServerRequest, HttpClient, Future<HttpClientRequest>> provider) {
        selector = provider;
        return this;
    }

    @Override
    public HttpProxyEx addInterceptor(ProxyInterceptorEx interceptor) {
        interceptors.add(interceptor);
        return this;
    }

    @Override
    public void handle(HttpServerRequest request) {
        handleEx(request, null);
    }

    /**
     * XXX 添加响应前的事件(复制handle方法)
     *
     * @param request        请求
     * @param beforeResponse 响应前的事件
     */
    @Override
    public void handleEx(HttpServerRequest request, Consumer<ProxyContext> beforeResponse) {
        ProxyRequest proxyRequest = ProxyRequest.reverseProxy(request);

        // Encoding sanity check
        Boolean chunked = HttpUtils.isChunked(request.headers());
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

        Proxy proxy = new Proxy(proxyRequest);
        proxy.filters = interceptors.listIterator();
        // XXX 以下是修改的部分
        if (beforeResponse == null) {
            proxy.sendRequest().compose(proxy::sendProxyResponse);
            return;
        }
        proxy.sendRequest().compose(proxy::sendProxyResponse).onSuccess(v -> beforeResponse.accept(proxy));
    }

    private void handleWebSocketUpgrade(ProxyRequest proxyRequest) {
        HttpServerRequest proxiedRequest = proxyRequest.proxiedRequest();
        resolveOrigin(proxiedRequest).onComplete(ar -> {
            if (ar.succeeded()) {
                HttpClientRequest request = ar.result();
                request.setMethod(HttpMethod.GET);
                request.setURI(proxiedRequest.uri());
                request.headers().addAll(proxiedRequest.headers());
                Future<HttpClientResponse> fut2 = request.connect();
                proxiedRequest.handler(request::write);
                proxiedRequest.endHandler(v -> request.end());
                proxiedRequest.resume();
                fut2.onComplete(ar2 -> {
                    if (ar2.succeeded()) {
                        HttpClientResponse proxiedResponse = ar2.result();
                        if (proxiedResponse.statusCode() == 101) {
                            HttpServerResponse response = proxiedRequest.response();
                            response.setStatusCode(101);
                            response.headers().addAll(proxiedResponse.headers());
                            Future<NetSocket> otherso = proxiedRequest.toNetSocket();
                            otherso.onComplete(ar3 -> {
                                if (ar3.succeeded()) {
                                    NetSocket responseSocket      = ar3.result();
                                    NetSocket proxyResponseSocket = proxiedResponse.netSocket();
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
        return selector.apply(proxiedRequest, client);
    }

    private class Proxy implements ProxyContext {

        private final ProxyRequest                     request;
        private       ProxyResponse                    response;
        private final Map<String, Object>              attachments = new HashMap<>();
        private       ListIterator<ProxyInterceptorEx> filters;

        private Proxy(ProxyRequest request) {
            this.request = request;
        }

        @Override
        public void set(String name, Object value) {
            attachments.put(name, value);
        }

        @Override
        public <T> T get(String name, Class<T> type) {
            Object o = attachments.get(name);
            return type.isInstance(o) ? type.cast(o) : null;
        }

        @Override
        public ProxyRequest request() {
            return request;
        }

        @Override
        public Future<ProxyResponse> sendRequest() {
            if (filters.hasNext()) {
                ProxyInterceptorEx next = filters.next();
                return next.handleProxyRequest(this);
            } else {
                return sendProxyRequest(request);
            }
        }

        @Override
        public ProxyResponse response() {
            return response;
        }

        @Override
        public Future<Void> sendResponse() {
            if (filters.hasPrevious()) {
                ProxyInterceptorEx filter = filters.previous();
                return filter.handleProxyResponse(this);
            } else {
                return response.send();
            }
        }

        private Future<ProxyResponse> sendProxyRequest(ProxyRequest proxyRequest) {
            Future<HttpClientRequest> f = resolveOrigin(proxyRequest.proxiedRequest());
            f.onFailure(err -> {
                // Should this be done here ? I don't think so
                HttpServerRequest proxiedRequest = proxyRequest.proxiedRequest();
                proxiedRequest.resume();
                Promise<Void> promise = Promise.promise();
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
            Boolean chunked = HttpUtils.isChunked(response.headers());
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

}
