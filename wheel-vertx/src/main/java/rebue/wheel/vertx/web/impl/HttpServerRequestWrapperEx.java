/**
 * XXX 复制4.3.7版本的io.vertx.ext.web.impl.HttpServerRequestWrapper
 * 可改变body
 */
package rebue.wheel.vertx.web.impl;

import io.netty.handler.codec.http.QueryStringDecoder;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.http.impl.HttpServerRequestInternal;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.AllowForwardHeaders;
import io.vertx.ext.web.impl.ServerWebSocketWrapper;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Wraps the source {@link HttpServerRequestInternal}. It updates the method, path and query of the original request and
 * resumes the request if a caller explicitly sets a handler to any callback that processes the request body.
 */
@Slf4j
public class HttpServerRequestWrapperEx extends io.vertx.core.http.impl.HttpServerRequestWrapper {

    private final ForwardedParser forwardedParser;

    private boolean modified;

    private HttpMethod method;
    private String path;
    private String query;
    private String uri;
    private String absoluteURI;
    private MultiMap params;

    // XXX 是否修改了body
    private boolean         modifiedBody = false;
    private Buffer          body;

    public HttpServerRequestWrapperEx(HttpServerRequest request, AllowForwardHeaders allowForward) {
        super((HttpServerRequestInternal) request);
        forwardedParser = new ForwardedParser(request, allowForward);
    }

    void changeTo(HttpMethod method, String uri) {
        modified = true;
        this.method = method;
        this.uri = uri;
        // lazy initialization
        this.query = null;
        this.absoluteURI = null;

        // parse
        int queryIndex = uri.indexOf('?');

        // there's a query
        if (queryIndex != -1) {
            int fragmentIndex = uri.indexOf('#', queryIndex);
            path = uri.substring(0, queryIndex);
            // there's a fragment
            if (fragmentIndex != -1) {
                query = uri.substring(queryIndex + 1, fragmentIndex);
            } else {
                query = uri.substring(queryIndex + 1);
            }
        } else {
            int fragmentIndex = uri.indexOf('#');
            // there's a fragment
            if (fragmentIndex != -1) {
                path = uri.substring(0, fragmentIndex);
            } else {
                path = uri;
            }
        }
    }

    /**
     * XXX 修改body
     */
    public void changTo(final Buffer body) {
        this.modifiedBody = true;
        this.body         = body;
    }

    @Override
    public Future<Buffer> body() {
        log.debug("HttpServerRequestWrapperEx.body(): modifiedBody-{}", this.modifiedBody);
        if (!this.modifiedBody) {
            return this.delegate.body();
        }
        return Future.succeededFuture(this.body);
    }

    @Override
    public HttpServerRequest body(final Handler<AsyncResult<Buffer>> handler) {
        log.debug("HttpServerRequestWrapperEx.body(handler): modifiedBody-{}", this.modifiedBody);
        if (this.modifiedBody) {
            body().onComplete(handler);
        } else {
            this.delegate.body(handler);
        }
        return this;
    }

    @Override
    public HttpMethod method() {
        if (!modified) {
            return delegate.method();
        }
        return method;
    }

    @Override
    public String uri() {
        if (!modified) {
            return delegate.uri();
        }
        return uri;
    }

    @Override
    public String path() {
        if (!modified) {
            return delegate.path();
        }
        return path;
    }

    @Override
    public String query() {
        if (!modified) {
            return delegate.query();
        }
        return query;
    }

    @Override
    public MultiMap params() {
        if (!modified) {
            return delegate.params();
        }
        if (params == null) {
            params = MultiMap.caseInsensitiveMultiMap();
            // if there is no query it's not really needed to parse it
            if (query != null) {
                QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri, Charset.forName(delegate.getParamsCharset()));
                Map<String, List<String>> prms = queryStringDecoder.parameters();
                if (!prms.isEmpty()) {
                    for (Map.Entry<String, List<String>> entry : prms.entrySet()) {
                        params.add(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        return params;
    }

    @Override
    public String getParam(String param) {
        if (!modified) {
            return delegate.getParam(param);
        }

        return params().get(param);
    }

    @Override
    public HttpServerRequest setParamsCharset(String s) {
        String old = delegate.getParamsCharset();
        delegate.setParamsCharset(s);
        if (!s.equals(old)) {
            params = null;
        }
        return this;
    }

    @Override
    public SocketAddress remoteAddress() {
        return forwardedParser.remoteAddress();
    }

    @Override
    public String absoluteURI() {
        if (!modified) {
            return forwardedParser.absoluteURI();
        } else {
            if (absoluteURI == null) {
                String scheme = forwardedParser.scheme();
                String host = forwardedParser.host();

                // if both are not null we can rebuild the uri
                if (scheme != null && host != null) {
                    absoluteURI = scheme + "://" + host + uri;
                } else {
                    absoluteURI = uri;
                }
            }

            return absoluteURI;
        }
    }

    @Override
    public String scheme() {
        return forwardedParser.scheme();
    }

    @Override
    public String host() {
        return forwardedParser.host();
    }

    @Override
    public void toWebSocket(Handler<AsyncResult<ServerWebSocket>> handler) {
        delegate
                .toWebSocket(toWebSocket -> {
                    if (toWebSocket.succeeded()) {
                        handler.handle(Future.succeededFuture(
                                new ServerWebSocketWrapper(toWebSocket.result(), host(), scheme(), isSSL(), remoteAddress())));
                    } else {
                        handler.handle(toWebSocket);
                    }
                });
    }

    @Override
    public Future<ServerWebSocket> toWebSocket() {
        return delegate
                .toWebSocket()
                .map(ws -> new ServerWebSocketWrapper(ws, host(), scheme(), isSSL(), remoteAddress()));
    }

    @Override
    public boolean isSSL() {
        return forwardedParser.isSSL();
    }

}
