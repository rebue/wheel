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
public class HttpServerRequestWrapperEx extends HttpServerRequestInternal {

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

    public HttpServerRequestWrapperEx(final HttpServerRequest request, final AllowForwardHeaders allowForward) {
        this.delegate        = (HttpServerRequestInternal) request;
        this.forwardedParser = new ForwardedParserEx(this.delegate, allowForward);
    }

    public void changeTo(final HttpMethod method, final String uri) {
        this.modified    = true;
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
            this.path = uri.substring(0, queryIndex);
            // there's a fragment
            if (fragmentIndex != -1) {
                this.query = uri.substring(queryIndex + 1, fragmentIndex);
            } else {
                this.query = uri.substring(queryIndex + 1);
            }
        } else {
            final int fragmentIndex = uri.indexOf('#');
            // there's a fragment
            if (fragmentIndex != -1) {
                this.path = uri.substring(0, fragmentIndex);
            } else {
                this.path = uri;
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
    public Pipe<Buffer> pipe() {
        log.debug("HttpServerRequestWrapperEx.pipe");
        if (!this.modifiedBody) {
            return this.delegate.pipe();
        }
        pause();
        return new PipeImplEx<>(this);
    }

    @Override
    public Future<Void> pipeTo(final WriteStream<Buffer> dst) {
        log.debug("HttpServerRequestWrapperEx.pipeTo");
        if (!this.modifiedBody) {
            return this.delegate.pipeTo(dst);
        }
        final Promise<Void> promise = Promise.promise();
        new PipeImplEx<>(this).to(dst, promise);
        return promise.future();
    }

    @Override
    public void pipeTo(final WriteStream<Buffer> dst, final Handler<AsyncResult<Void>> handler) {
        log.debug("HttpServerRequestWrapperEx.pipeTo");
        if (!this.modifiedBody) {
            this.delegate.pipeTo(dst, handler);
        } else {
            new PipeImplEx<>(this).to(dst, handler);
        }
    }

    @Override
    public DecoderResult decoderResult() {
        log.debug("HttpServerRequestWrapperEx.decoderResult");
        return this.delegate.decoderResult();
    }

    @Override
    public long bytesRead() {
        log.debug("HttpServerRequestWrapperEx.bytesRead");
        return this.delegate.bytesRead();
    }

    @Override
    public HttpServerRequest exceptionHandler(final Handler<Throwable> handler) {
        log.debug("HttpServerRequestWrapperEx.exceptionHandler");
        this.delegate.exceptionHandler(handler);
        return this;
    }

    @Override
    public HttpServerRequest handler(final Handler<Buffer> handler) {
        log.debug("HttpServerRequestWrapperEx.handler");
        if (!this.modifiedBody) {
            this.delegate.handler(handler);
        } else {
            this.handler = handler;
        }

        return this;
    }

    @Override
    public HttpServerRequest pause() {
        log.debug("HttpServerRequestWrapperEx.pause");
        if (!this.modifiedBody) {
            this.delegate.pause();
        }
        return this;
    }

    @Override
    public HttpServerRequest resume() {
        log.debug("HttpServerRequestWrapperEx.resume");
        if (!this.modifiedBody) {
            this.delegate.resume();
        } else {
            fetch(Long.MAX_VALUE);
        }
        return this;
    }

    @Override
    public HttpServerRequest fetch(final long amount) {
        log.debug("HttpServerRequestWrapperEx.fetch");
        if (!this.modifiedBody) {
            this.delegate.fetch(amount);
        } else {
            this.handler.handle(this.body);
            this.endHandler.handle(null);
        }
        return this;
    }

    @Override
    public HttpServerRequest endHandler(final Handler<Void> handler) {
        log.debug("HttpServerRequestWrapperEx.endHandler");
        if (!this.modifiedBody) {
            this.delegate.endHandler(handler);
        } else {
            this.endHandler = handler;
        }
        return this;
    }

    @Override
    public HttpVersion version() {
        log.debug("HttpServerRequestWrapperEx.version");
        return this.delegate.version();
    }

    @Override
    public HttpMethod method() {
        log.debug("HttpServerRequestWrapperEx.method");
        if (!this.modified) {
            return this.delegate.method();
        }
        return this.method;
    }

    @Override
    public String uri() {
        log.debug("HttpServerRequestWrapperEx.uri");
        if (!this.modified) {
            return this.delegate.uri();
        }
        return this.uri;
    }

    @Override
    public String path() {
        log.debug("HttpServerRequestWrapperEx.path");
        if (!this.modified) {
            return this.delegate.path();
        }
        return this.path;
    }

    @Override
    public String query() {
        log.debug("HttpServerRequestWrapperEx.query");
        if (!this.modified) {
            return this.delegate.query();
        }
        return this.query;
    }

    @Override
    public MultiMap params() {
        log.debug("HttpServerRequestWrapperEx.params");
        if (!this.modified) {
            return this.delegate.params();
        }
        if (this.params == null) {
            this.params = MultiMap.caseInsensitiveMultiMap();
            // if there is no query it's not really needed to parse it
            if (this.query != null) {
                final QueryStringDecoder        queryStringDecoder = new QueryStringDecoder(this.uri, Charset.forName(this.delegate.getParamsCharset()));
                final Map<String, List<String>> prms               = queryStringDecoder.parameters();
                if (!prms.isEmpty()) {
                    for (final Map.Entry<String, List<String>> entry : prms.entrySet()) {
                        this.params.add(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        return this.params;
    }

    @Override
    public String getParam(final String param) {
        log.debug("HttpServerRequestWrapperEx.getParam");
        if (!this.modified) {
            return this.delegate.getParam(param);
        }

        return params().get(param);
    }

    @Override
    public HttpServerResponse response() {
        log.debug("HttpServerRequestWrapperEx.response");
        return this.delegate.response();
    }

    @Override
    public MultiMap headers() {
        log.debug("HttpServerRequestWrapperEx.headers");
        return this.delegate.headers();
    }

    @Override
    public String getHeader(final String s) {
        log.debug("HttpServerRequestWrapperEx.getHeader");
        return this.delegate.getHeader(s);
    }

    @Override
    public String getHeader(final CharSequence charSequence) {
        log.debug("HttpServerRequestWrapperEx.getHeader");
        return this.delegate.getHeader(charSequence);
    }

    @Override
    public HttpServerRequest setParamsCharset(final String s) {
        log.debug("HttpServerRequestWrapperEx.setParamsCharset");
        final String old = this.delegate.getParamsCharset();
        this.delegate.setParamsCharset(s);
        if (!s.equals(old)) {
            this.params = null;
        }
        return this;
    }

    @Override
    public String getParamsCharset() {
        log.debug("HttpServerRequestWrapperEx.getParamsCharset");
        return this.delegate.getParamsCharset();
    }

    @Override
    public SocketAddress remoteAddress() {
        log.debug("HttpServerRequestWrapperEx.remoteAddress");
        return this.forwardedParser.remoteAddress();
    }

    @Override
    public SocketAddress localAddress() {
        log.debug("HttpServerRequestWrapperEx.localAddress");
        return this.delegate.localAddress();
    }

    @Override
    @Deprecated
    public X509Certificate[] peerCertificateChain() throws SSLPeerUnverifiedException {
        log.debug("HttpServerRequestWrapperEx.peerCertificateChain");
        return this.delegate.peerCertificateChain();
    }

    @Override
    public SSLSession sslSession() {
        log.debug("HttpServerRequestWrapperEx.sslSession");
        return this.delegate.sslSession();
    }

    @Override
    public String absoluteURI() {
        log.debug("HttpServerRequestWrapperEx.absoluteURI");
        if (!this.modified) {
            return this.forwardedParser.absoluteURI();
        }
        if (this.absoluteURI == null) {
            final String scheme = this.forwardedParser.scheme();
            final String host   = this.forwardedParser.host();

            // if both are not null we can rebuild the uri
            if (scheme != null && host != null) {
                this.absoluteURI = scheme + "://" + host + this.uri;
            } else {
                this.absoluteURI = this.uri;
            }
        }

        return this.absoluteURI;
    }

    @Override
    public String scheme() {
        log.debug("HttpServerRequestWrapperEx.scheme");
        return this.forwardedParser.scheme();
    }

    @Override
    public String host() {
        log.debug("HttpServerRequestWrapperEx.host");
        return this.forwardedParser.host();
    }

    @Override
    public HttpServerRequest customFrameHandler(final Handler<HttpFrame> handler) {
        log.debug("HttpServerRequestWrapperEx.customFrameHandler");
        this.delegate.customFrameHandler(handler);
        return this;
    }

    @Override
    public HttpConnection connection() {
        log.debug("HttpServerRequestWrapperEx.connection");
        return this.delegate.connection();
    }

    @Override
    public HttpServerRequest bodyHandler(final Handler<Buffer> handler) {
        log.debug("HttpServerRequestWrapperEx.bodyHandler");
        this.delegate.bodyHandler(handler);
        return this;
    }

    @Override
    public void toNetSocket(final Handler<AsyncResult<NetSocket>> handler) {
        log.debug("HttpServerRequestWrapperEx.toNetSocket");
        this.delegate.toNetSocket(handler);
    }

    @Override
    public Future<NetSocket> toNetSocket() {
        log.debug("HttpServerRequestWrapperEx.toNetSocket");
        return this.delegate.toNetSocket();
    }

    @Override
    public HttpServerRequest setExpectMultipart(final boolean b) {
        log.debug("HttpServerRequestWrapperEx.setExpectMultipart");
        this.delegate.setExpectMultipart(b);
        return this;
    }

    @Override
    public boolean isExpectMultipart() {
        log.debug("HttpServerRequestWrapperEx.isExpectMultipart");
        return this.delegate.isExpectMultipart();
    }

    @Override
    public HttpServerRequest uploadHandler(final Handler<HttpServerFileUpload> handler) {
        log.debug("HttpServerRequestWrapperEx.uploadHandler");
        this.delegate.uploadHandler(handler);
        return this;
    }

    @Override
    public MultiMap formAttributes() {
        log.debug("HttpServerRequestWrapperEx.formAttributes");
        return this.delegate.formAttributes();
    }

    @Override
    public String getFormAttribute(final String s) {
        log.debug("HttpServerRequestWrapperEx.getFormAttribute");
        return this.delegate.getFormAttribute(s);
    }

    @Override
    public int streamId() {
        log.debug("HttpServerRequestWrapperEx.streamId");
        return this.delegate.streamId();
    }

    @Override
    public void toWebSocket(final Handler<AsyncResult<ServerWebSocket>> handler) {
        log.debug("HttpServerRequestWrapperEx.toWebSocket");
        this.delegate
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
        return this.delegate
                .toWebSocket()
                .map(ws -> new ServerWebSocketWrapper(ws, host(), scheme(), isSSL(), remoteAddress()));
    }

    @Override
    public boolean isEnded() {
        log.debug("HttpServerRequestWrapperEx.isEnded");
        return this.delegate.isEnded();
    }

    @Override
    public boolean isSSL() {
        log.debug("HttpServerRequestWrapperEx.isSSL");
        return this.forwardedParser.isSSL();
    }

    @Override
    public HttpServerRequest streamPriorityHandler(final Handler<StreamPriority> handler) {
        log.debug("HttpServerRequestWrapperEx.streamPriorityHandler");
        this.delegate.streamPriorityHandler(handler);
        return this;
    }

    @Override
    public StreamPriority streamPriority() {
        log.debug("HttpServerRequestWrapperEx.streamPriority");
        return this.delegate.streamPriority();
    }

    @Override
    public @Nullable Cookie getCookie(final String name) {
        log.debug("HttpServerRequestWrapperEx.getCookie");
        return this.delegate.getCookie(name);
    }

    @Override
    public @Nullable Cookie getCookie(final String name, final String domain, final String path) {
        log.debug("HttpServerRequestWrapperEx.getCookie");
        return this.delegate.getCookie(name, domain, path);
    }

    @Override
    public String getParam(final String paramName, final String defaultValue) {
        log.debug("HttpServerRequestWrapperEx.getParam");
        return this.delegate.getParam(paramName, defaultValue);
    }

    @Override
    public Set<Cookie> cookies(final String name) {
        log.debug("HttpServerRequestWrapperEx.cookies");
        return this.delegate.cookies(name);
    }

    @Override
    public Set<Cookie> cookies() {
        log.debug("HttpServerRequestWrapperEx.cookies");
        return this.delegate.cookies();
    }

    @Override
    public void end(final Handler<AsyncResult<Void>> handler) {
        log.debug("HttpServerRequestWrapperEx.end");
        this.delegate.end(handler);
    }

    @Override
    public Future<Void> end() {
        log.debug("HttpServerRequestWrapperEx.end");
        return this.delegate.end();
    }

    @Override
    public HttpServerRequest routed(final String route) {
        log.debug("HttpServerRequestWrapperEx.routed");
        this.delegate.routed(route);
        return this;
    }

    @Override
    public Context context() {
        log.debug("HttpServerRequestWrapperEx.context");
        return this.delegate.context();
    }

    @Override
    public Object metric() {
        log.debug("HttpServerRequestWrapperEx.metric");
        return this.delegate.metric();
    }

}
