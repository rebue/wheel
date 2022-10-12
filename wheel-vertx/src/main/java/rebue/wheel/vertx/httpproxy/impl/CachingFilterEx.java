/**
 * XXX 复制io.vertx.httpproxy.impl.CachingFilter类的代码，原类会让ctx的后置处理器失效
 */
package rebue.wheel.vertx.httpproxy.impl;

import java.util.Date;
import java.util.function.BiFunction;

import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.httpproxy.Body;
import io.vertx.httpproxy.ProxyContext;
import io.vertx.httpproxy.ProxyRequest;
import io.vertx.httpproxy.ProxyResponse;
import io.vertx.httpproxy.impl.ParseUtils;
import io.vertx.httpproxy.spi.cache.Cache;
import rebue.wheel.vertx.httpproxy.ProxyInterceptorEx;

class CachingFilterEx implements ProxyInterceptorEx {

    private static final BiFunction<String, ResourceEx, ResourceEx> CACHE_GET_AND_VALIDATE = (key, resource) -> {
                                                                                               final long now = System.currentTimeMillis();
                                                                                               final long val = resource.timestamp + resource.maxAge;
                                                                                               return val < now ? null : resource;
                                                                                           };

    private final Cache<String, ResourceEx>                         cache;

    public CachingFilterEx(Cache<String, ResourceEx> cache) {
        this.cache = cache;
    }

    @Override
    public Future<ProxyResponse> handleProxyRequest(ProxyContext context) {
        final Future<ProxyResponse> future = tryHandleProxyRequestFromCache(context);
        if (future != null) {
            return future;
        }
        return context.sendRequest();
    }

    @Override
    public Future<Void> handleProxyResponse(ProxyContext context) {
        return sendAndTryCacheProxyResponse(context);
    }

    private Future<Void> sendAndTryCacheProxyResponse(ProxyContext context) {

        final ProxyResponse response = context.response();
        final ResourceEx    cached   = context.get("cached_resource", ResourceEx.class);

        if (cached != null && response.getStatusCode() == 304) {
            // Warning: this relies on the fact that HttpServerRequest will not send a body for HEAD
            response.release();
            cached.init(response);
            return context.sendResponse();
        }

        final ProxyRequest request = response.request();
        if (!response.publicCacheControl() || response.maxAge() <= 0) {
            return context.sendResponse();
        }
        if (request.getMethod() == HttpMethod.GET) {
            final String     absoluteUri = request.absoluteURI();
            final ResourceEx res         = new ResourceEx(
                    absoluteUri,
                    response.getStatusCode(),
                    response.getStatusMessage(),
                    response.headers(),
                    System.currentTimeMillis(),
                    response.maxAge());
            final Body       body        = response.getBody();
            response.setBody(Body.body(new BufferingReadStreamEx(body.stream(), res.content), body.length()));
            final Future<Void> fut = context.sendResponse();
            fut.onSuccess(v -> {
                cache.put(absoluteUri, res);
            });
            return fut;
        }
        if (request.getMethod() == HttpMethod.HEAD) {
            final ResourceEx resource = cache.get(request.absoluteURI());
            if (resource != null) {
                if (!revalidateResource(response, resource)) {
                    // Invalidate cache
                    cache.remove(request.absoluteURI());
                }
            }
        }
        return context.sendResponse();
    }

    private static boolean revalidateResource(ProxyResponse response, ResourceEx resource) {
        if (resource.etag != null && response.etag() != null) {
            return resource.etag.equals(response.etag());
        }
        return true;
    }

    private Future<ProxyResponse> tryHandleProxyRequestFromCache(ProxyContext context) {

        final ProxyRequest      proxyRequest = context.request();

        final HttpServerRequest response     = proxyRequest.proxiedRequest();

        ResourceEx              resource;
        final HttpMethod        method       = response.method();
        if (method != HttpMethod.GET && method != HttpMethod.HEAD) {
            return null;
        }
        final String cacheKey = proxyRequest.absoluteURI();
        resource = cache.computeIfPresent(cacheKey, CACHE_GET_AND_VALIDATE);
        if (resource == null) {
            return null;
        }

        final String cacheControlHeader = response.getHeader(HttpHeaders.CACHE_CONTROL);
        if (cacheControlHeader != null) {
            final CacheControlEx cacheControlEx = new CacheControlEx().parse(cacheControlHeader);
            if (cacheControlEx.maxAge() >= 0) {
                final long now        = System.currentTimeMillis();
                final long currentAge = now - resource.timestamp;
                if (currentAge > cacheControlEx.maxAge() * 1000) {
                    final String etag = resource.headers.get(HttpHeaders.ETAG);
                    if (etag != null) {
                        proxyRequest.headers().set(HttpHeaders.IF_NONE_MATCH, resource.etag);
                        context.set("cached_resource", resource);
                        return context.sendRequest();
                    }
                    return null;
                }
            }
        }

        //
        final String ifModifiedSinceHeader = response.getHeader(HttpHeaders.IF_MODIFIED_SINCE);
        if ((response.method() == HttpMethod.GET || response.method() == HttpMethod.HEAD) && ifModifiedSinceHeader != null && resource.lastModified != null) {
            final Date ifModifiedSince = ParseUtils.parseHeaderDate(ifModifiedSinceHeader);
            if (resource.lastModified.getTime() <= ifModifiedSince.getTime()) {
                response.response().setStatusCode(304).end();
                return Future.succeededFuture();
            }
        }
        proxyRequest.release();
        final ProxyResponse proxyResponse = proxyRequest.response();
        resource.init(proxyResponse);
        return Future.succeededFuture(proxyResponse);
    }
}
