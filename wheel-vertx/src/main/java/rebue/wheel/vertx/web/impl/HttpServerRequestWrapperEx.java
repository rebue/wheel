/**
 * XXX 复制io.vertx.ext.web.impl.HttpServerRequestWrapper，可改变body
 */
package rebue.wheel.vertx.web.impl;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.cert.X509Certificate;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpConnection;
import io.vertx.core.http.HttpFrame;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerFileUpload;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.StreamPriority;
import io.vertx.core.http.impl.HttpServerRequestInternal;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.streams.Pipe;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.web.AllowForwardHeaders;
import io.vertx.ext.web.impl.ServerWebSocketWrapper;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.stream.impl.PipeImplEx;

/**
 * Wraps the source {@link HttpServerRequestInternal}. It updates the method, path and query of the original request and
 * resumes the request if a caller explicitly sets a handler to any callback that processes the request body.
 */
@Slf4j
public class HttpServerRequestWrapperEx implements HttpServerRequestInternal {

    private final HttpServerRequestInternal delegate;
    private final ForwardedParserEx         forwardedParser;

    private boolean                         modified;
    private HttpMethod                      method;
    private String                          path;
    private String                          query;
    private String                          uri;
    private String                          absoluteURI;
    private MultiMap                        params;

    // XXX 是否修改了body
    private boolean         modifiedBody = false;
    private Buffer          body;
    private Handler<Buffer> handler;
    private Handler<Void>   endHandler;

    public HttpServerRequestWrapperEx(HttpServerRequest request, AllowForwardHeaders allowForward) {
        delegate        = (HttpServerRequestInternal) request;
        forwardedParser = new ForwardedParserEx(delegate, allowForward);
    }

    public void changeTo(HttpMethod method, String uri) {
        modified         = true;
        this.method      = method;
        this.uri         = uri;
        // lazy initialization
        this.query       = null;
        this.absoluteURI = null;

        // parse
        final int queryIndex = uri.indexOf('?');

        // there's a query
        if (queryIndex != -1) {
            final int fragmentIndex = uri.indexOf('#', queryIndex);
            path = uri.substring(0, queryIndex);
            // there's a fragment
            if (fragmentIndex != -1) {
                query = uri.substring(queryIndex + 1, fragmentIndex);
            } else {
                query = uri.substring(queryIndex + 1);
            }
        } else {
            final int fragmentIndex = uri.indexOf('#');
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
    public void changTo(Buffer body) {
        modifiedBody = true;
        this.body    = body;
    }

    @Override
    public Future<Buffer> body() {
        log.debug("HttpServerRequestWrapperEx.body(): modifiedBody-{}", modifiedBody);
        if (!modifiedBody) {
            return delegate.body();
        }
        return Future.succeededFuture(this.body);
    }

    @Override
    public HttpServerRequest body(Handler<AsyncResult<Buffer>> handler) {
        log.debug("HttpServerRequestWrapperEx.body(handler): modifiedBody-{}", modifiedBody);
        if (modifiedBody) {
            body().onComplete(handler);
        } else {
            delegate.body(handler);
        }
        return this;
    }

    @Override
    public Pipe<Buffer> pipe() {
        log.debug("HttpServerRequestWrapperEx.pipe");
        if (!modifiedBody) {
            return delegate.pipe();
        }
        pause();
        return new PipeImplEx<>(this);
    }

    @Override
    public Future<Void> pipeTo(WriteStream<Buffer> dst) {
        log.debug("HttpServerRequestWrapperEx.pipeTo");
        if (!modifiedBody) {
            return delegate.pipeTo(dst);
        }
        final Promise<Void> promise = Promise.promise();
        new PipeImplEx<>(this).to(dst, promise);
        return promise.future();
    }

    @Override
    public void pipeTo(WriteStream<Buffer> dst, Handler<AsyncResult<Void>> handler) {
        log.debug("HttpServerRequestWrapperEx.pipeTo");
        if (!modifiedBody) {
            delegate.pipeTo(dst, handler);
        } else {
            new PipeImplEx<>(this).to(dst, handler);
        }
    }

    @Override
    public DecoderResult decoderResult() {
        log.debug("HttpServerRequestWrapperEx.decoderResult");
        return delegate.decoderResult();
    }

    @Override
    public long bytesRead() {
        log.debug("HttpServerRequestWrapperEx.bytesRead");
        return delegate.bytesRead();
    }

    @Override
    public HttpServerRequest exceptionHandler(Handler<Throwable> handler) {
        log.debug("HttpServerRequestWrapperEx.exceptionHandler");
        delegate.exceptionHandler(handler);
        return this;
    }

    @Override
    public HttpServerRequest handler(Handler<Buffer> handler) {
        log.debug("HttpServerRequestWrapperEx.handler");
        if (!modifiedBody) {
            delegate.handler(handler);
        } else {
            this.handler = handler;
        }

        return this;
    }

    @Override
    public HttpServerRequest pause() {
        log.debug("HttpServerRequestWrapperEx.pause");
        if (!modifiedBody) {
            delegate.pause();
        }
        return this;
    }

    @Override
    public HttpServerRequest resume() {
        log.debug("HttpServerRequestWrapperEx.resume");
        if (!modifiedBody) {
            delegate.resume();
        } else {
            fetch(Long.MAX_VALUE);
        }
        return this;
    }

    @Override
    public HttpServerRequest fetch(long amount) {
        log.debug("HttpServerRequestWrapperEx.fetch");
        if (!modifiedBody) {
            delegate.fetch(amount);
        } else {
            this.handler.handle(body);
            this.endHandler.handle(null);
        }
        return this;
    }

    @Override
    public HttpServerRequest endHandler(Handler<Void> handler) {
        log.debug("HttpServerRequestWrapperEx.endHandler");
        if (!modifiedBody) {
            delegate.endHandler(handler);
        } else {
            this.endHandler = handler;
        }
        return this;
    }

    @Override
    public HttpVersion version() {
        log.debug("HttpServerRequestWrapperEx.version");
        return delegate.version();
    }

    @Override
    public HttpMethod method() {
        log.debug("HttpServerRequestWrapperEx.method");
        if (!modified) {
            return delegate.method();
        }
        return method;
    }

    @Override
    public String uri() {
        log.debug("HttpServerRequestWrapperEx.uri");
        if (!modified) {
            return delegate.uri();
        }
        return uri;
    }

    @Override
    public String path() {
        log.debug("HttpServerRequestWrapperEx.path");
        if (!modified) {
            return delegate.path();
        }
        return path;
    }

    @Override
    public String query() {
        log.debug("HttpServerRequestWrapperEx.query");
        if (!modified) {
            return delegate.query();
        }
        return query;
    }

    @Override
    public MultiMap params() {
        log.debug("HttpServerRequestWrapperEx.params");
        if (!modified) {
            return delegate.params();
        }
        if (params == null) {
            params = MultiMap.caseInsensitiveMultiMap();
            // if there is no query it's not really needed to parse it
            if (query != null) {
                final QueryStringDecoder        queryStringDecoder = new QueryStringDecoder(uri, Charset.forName(delegate.getParamsCharset()));
                final Map<String, List<String>> prms               = queryStringDecoder.parameters();
                if (!prms.isEmpty()) {
                    for (final Map.Entry<String, List<String>> entry : prms.entrySet()) {
                        params.add(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        return params;
    }

    @Override
    public String getParam(String param) {
        log.debug("HttpServerRequestWrapperEx.getParam");
        if (!modified) {
            return delegate.getParam(param);
        }

        return params().get(param);
    }

    @Override
    public HttpServerResponse response() {
        log.debug("HttpServerRequestWrapperEx.response");
        return delegate.response();
    }

    @Override
    public MultiMap headers() {
        log.debug("HttpServerRequestWrapperEx.headers");
        return delegate.headers();
    }

    @Override
    public String getHeader(String s) {
        log.debug("HttpServerRequestWrapperEx.getHeader");
        return delegate.getHeader(s);
    }

    @Override
    public String getHeader(CharSequence charSequence) {
        log.debug("HttpServerRequestWrapperEx.getHeader");
        return delegate.getHeader(charSequence);
    }

    @Override
    public HttpServerRequest setParamsCharset(String s) {
        log.debug("HttpServerRequestWrapperEx.setParamsCharset");
        final String old = delegate.getParamsCharset();
        delegate.setParamsCharset(s);
        if (!s.equals(old)) {
            params = null;
        }
        return this;
    }

    @Override
    public String getParamsCharset() {
        log.debug("HttpServerRequestWrapperEx.getParamsCharset");
        return delegate.getParamsCharset();
    }

    @Override
    public SocketAddress remoteAddress() {
        log.debug("HttpServerRequestWrapperEx.remoteAddress");
        return forwardedParser.remoteAddress();
    }

    @Override
    public SocketAddress localAddress() {
        log.debug("HttpServerRequestWrapperEx.localAddress");
        return delegate.localAddress();
    }

    @Override
    @Deprecated
    public X509Certificate[] peerCertificateChain() throws SSLPeerUnverifiedException {
        log.debug("HttpServerRequestWrapperEx.peerCertificateChain");
        return delegate.peerCertificateChain();
    }

    @Override
    public SSLSession sslSession() {
        log.debug("HttpServerRequestWrapperEx.sslSession");
        return delegate.sslSession();
    }

    @Override
    public String absoluteURI() {
        log.debug("HttpServerRequestWrapperEx.absoluteURI");
        if (!modified) {
            return forwardedParser.absoluteURI();
        }
        if (absoluteURI == null) {
            final String scheme = forwardedParser.scheme();
            final String host   = forwardedParser.host();

            // if both are not null we can rebuild the uri
            if (scheme != null && host != null) {
                absoluteURI = scheme + "://" + host + uri;
            } else {
                absoluteURI = uri;
            }
        }

        return absoluteURI;
    }

    @Override
    public String scheme() {
        log.debug("HttpServerRequestWrapperEx.scheme");
        return forwardedParser.scheme();
    }

    @Override
    public String host() {
        log.debug("HttpServerRequestWrapperEx.host");
        return forwardedParser.host();
    }

    @Override
    public HttpServerRequest customFrameHandler(Handler<HttpFrame> handler) {
        log.debug("HttpServerRequestWrapperEx.customFrameHandler");
        delegate.customFrameHandler(handler);
        return this;
    }

    @Override
    public HttpConnection connection() {
        log.debug("HttpServerRequestWrapperEx.connection");
        return delegate.connection();
    }

    @Override
    public HttpServerRequest bodyHandler(Handler<Buffer> handler) {
        log.debug("HttpServerRequestWrapperEx.bodyHandler");
        delegate.bodyHandler(handler);
        return this;
    }

    @Override
    public void toNetSocket(Handler<AsyncResult<NetSocket>> handler) {
        log.debug("HttpServerRequestWrapperEx.toNetSocket");
        delegate.toNetSocket(handler);
    }

    @Override
    public Future<NetSocket> toNetSocket() {
        log.debug("HttpServerRequestWrapperEx.toNetSocket");
        return delegate.toNetSocket();
    }

    @Override
    public HttpServerRequest setExpectMultipart(boolean b) {
        log.debug("HttpServerRequestWrapperEx.setExpectMultipart");
        delegate.setExpectMultipart(b);
        return this;
    }

    @Override
    public boolean isExpectMultipart() {
        log.debug("HttpServerRequestWrapperEx.isExpectMultipart");
        return delegate.isExpectMultipart();
    }

    @Override
    public HttpServerRequest uploadHandler(Handler<HttpServerFileUpload> handler) {
        log.debug("HttpServerRequestWrapperEx.uploadHandler");
        delegate.uploadHandler(handler);
        return this;
    }

    @Override
    public MultiMap formAttributes() {
        log.debug("HttpServerRequestWrapperEx.formAttributes");
        return delegate.formAttributes();
    }

    @Override
    public String getFormAttribute(String s) {
        log.debug("HttpServerRequestWrapperEx.getFormAttribute");
        return delegate.getFormAttribute(s);
    }

    @Override
    public int streamId() {
        log.debug("HttpServerRequestWrapperEx.streamId");
        return delegate.streamId();
    }

    @Override
    public void toWebSocket(Handler<AsyncResult<ServerWebSocket>> handler) {
        log.debug("HttpServerRequestWrapperEx.toWebSocket");
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
        log.debug("HttpServerRequestWrapperEx.toWebSocket");
        return delegate
                .toWebSocket()
                .map(ws -> new ServerWebSocketWrapper(ws, host(), scheme(), isSSL(), remoteAddress()));
    }

    @Override
    public boolean isEnded() {
        log.debug("HttpServerRequestWrapperEx.isEnded");
        return delegate.isEnded();
    }

    @Override
    public boolean isSSL() {
        log.debug("HttpServerRequestWrapperEx.isSSL");
        return forwardedParser.isSSL();
    }

    @Override
    public HttpServerRequest streamPriorityHandler(Handler<StreamPriority> handler) {
        log.debug("HttpServerRequestWrapperEx.streamPriorityHandler");
        delegate.streamPriorityHandler(handler);
        return this;
    }

    @Override
    public StreamPriority streamPriority() {
        log.debug("HttpServerRequestWrapperEx.streamPriority");
        return delegate.streamPriority();
    }

    @Override
    public @Nullable Cookie getCookie(String name) {
        log.debug("HttpServerRequestWrapperEx.getCookie");
        return delegate.getCookie(name);
    }

    @Override
    public @Nullable Cookie getCookie(String name, String domain, String path) {
        log.debug("HttpServerRequestWrapperEx.getCookie");
        return delegate.getCookie(name, domain, path);
    }

    @Override
    public String getParam(String paramName, String defaultValue) {
        log.debug("HttpServerRequestWrapperEx.getParam");
        return delegate.getParam(paramName, defaultValue);
    }

    @Override
    public Set<Cookie> cookies(String name) {
        log.debug("HttpServerRequestWrapperEx.cookies");
        return delegate.cookies(name);
    }

    @Override
    public Set<Cookie> cookies() {
        log.debug("HttpServerRequestWrapperEx.cookies");
        return delegate.cookies();
    }

    @Override
    public void end(Handler<AsyncResult<Void>> handler) {
        log.debug("HttpServerRequestWrapperEx.end");
        delegate.end(handler);
    }

    @Override
    public Future<Void> end() {
        log.debug("HttpServerRequestWrapperEx.end");
        return delegate.end();
    }

    @Override
    public HttpServerRequest routed(String route) {
        log.debug("HttpServerRequestWrapperEx.routed");
        delegate.routed(route);
        return this;
    }

    @Override
    public Context context() {
        log.debug("HttpServerRequestWrapperEx.context");
        return delegate.context();
    }

    @Override
    public Object metric() {
        log.debug("HttpServerRequestWrapperEx.metric");
        return delegate.metric();
    }

}
