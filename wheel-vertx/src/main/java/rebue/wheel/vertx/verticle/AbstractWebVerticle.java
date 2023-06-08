package rebue.wheel.vertx.verticle;

import com.google.inject.Injector;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.impl.Arguments;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SelfSignedCertificate;
import io.vertx.ext.web.AllowForwardHeaders;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.config.WebProperties;
import rebue.wheel.vertx.guice.InjectorVerticle;

import java.util.Map;

@Slf4j
public abstract class AbstractWebVerticle extends AbstractVerticle implements InjectorVerticle {
    private HttpServer httpServer;
    private HttpServer http2httpsServer;

    @Inject
    @Named("mainId")
    private String mainId;

    protected Injector injector;

    public void setInjector(Injector injector) {
        this.injector = injector;
    }

    private MessageConsumer<Void> startConsumer;

    @Override
    public void start() {
        log.info("WebVerticle start preparing");

        WebProperties webProperties = config().mapTo(WebProperties.class);
        final HttpServerOptions httpServerOptions = webProperties.getServer() == null ? new HttpServerOptions()
                : new HttpServerOptions(JsonObject.mapFrom(webProperties.getServer()));

        log.info("创建路由");
        final Router router = Router.router(this.vertx);

        AllowForwardHeaders allowForwardHeaders = AllowForwardHeaders.valueOf(webProperties.getAllowForward());
        log.info("设置allow forward: {}", allowForwardHeaders);
        router.allowForward(allowForwardHeaders);

        // 全局route
        final Route globalRoute = router.route();
        // 响应内容类型处理(处理器会通过 getAcceptableContentType 方法来选择适当的内容类型)
        globalRoute.handler(ResponseContentTypeHandler.create());
        // 全局返回响应时间
        if (webProperties.getIsResponseTime()) {
            log.info("开启返回响应时间");
            globalRoute.handler(ResponseTimeHandler.create());
        }
        // 超时时间
        final Long timeout = webProperties.getTimeout();
        if (timeout != null && timeout != 0) {
            final Integer timeoutErrorCode = webProperties.getTimeoutErrorCode();
            final int     errorCode        = timeoutErrorCode != null ? timeoutErrorCode : 503;
            log.info("开启超时返回错误状态码{}", errorCode);
            globalRoute.handler(TimeoutHandler.create(timeout, errorCode));
        }
        // 记录日志
        if (webProperties.getIsLogging()) {
            log.info("开启Web日志记录");
            globalRoute.handler(LoggerHandler.create(webProperties.getLoggerFormat()));
        }
        // CORS
        if (webProperties.getIsCors()) {
            log.info("开启CORS");
//            globalRoute.handler(CorsHandler.create("*")
//                    .allowedMethod(HttpMethod.GET)
//                    .allowedMethod(HttpMethod.POST)
//                    .allowedMethod(HttpMethod.PUT)
//                    .allowedMethod(HttpMethod.DELETE)
//                    .allowedMethod(HttpMethod.PATCH)
//                    .allowedMethod(HttpMethod.OPTIONS));
            globalRoute.handler(CorsHandler.create());
        }
        // 全局路由错误处理
        final ErrorHandler errorHandler = ErrorHandler.create(this.vertx);
        globalRoute.failureHandler(ctx -> {
            log.error("全局路由错误处理: {}", ctx.statusCode());
            errorHandler.handle(ctx);
        });

        if (webProperties.getSelfSignedCertificate()) {
            log.info("实现自签名证书");
            SelfSignedCertificate certificate = SelfSignedCertificate.create();
            httpServerOptions
                    .setSsl(true)
                    .setKeyCertOptions(certificate.keyCertOptions())
                    .setTrustOptions(certificate.trustOptions());
        }

        log.info("配置路由器");
        configRouter(router);

        this.httpServer = this.vertx.createHttpServer(httpServerOptions).requestHandler(router);

        Map<String, Object> http2https = webProperties.getHttp2https();
        if (http2https != null) {
            final HttpServerOptions http2httpsServerOptions = new HttpServerOptions(JsonObject.mapFrom(webProperties.getHttp2https()));
            int                     http2httpsPort          = http2httpsServerOptions.getPort();
            int                     httpsPort               = httpServerOptions.getPort();
            Arguments.require(http2httpsPort != 0, "web.config.http2https.port不能为null或0");
            Arguments.require(httpsPort != 0, "web.config.server.port不能为null或0");

            this.http2httpsServer = this.vertx.createHttpServer(http2httpsServerOptions)
                    .requestHandler(req -> req.response()
                            .setStatusCode(301)
                            .putHeader("Location", req.absoluteURI()
                                    .replace("http", "https")
                                    .replace(":" + http2httpsPort, ":" + httpsPort)
                            )
                            .end());
        }

        final String address = AbstractMainVerticle.EVENT_BUS_DEPLOY_SUCCESS + "::" + this.mainId;
        log.info("WebVerticle配置消费EventBus事件-MainVerticle部署成功事件: {}", address);
        this.startConsumer = this.vertx.eventBus().consumer(address, this::handleStart);
        this.startConsumer.completionHandler(this::handleStartCompletion);

        log.info("WebVerticle end preparing");
    }

    @Override
    public void stop() {
        log.info("WebVerticle stop");
        if (http2httpsServer != null) http2httpsServer.close();
        this.httpServer.close();
    }

    /**
     * 配置路由
     *
     * @param router 路由器
     */
    protected abstract void configRouter(Router router);

    private void handleStart(final Message<Void> message) {
        log.info("WebVerticle start");
        this.startConsumer.unregister();
        this.httpServer.listen(res -> {
            if (res.succeeded()) {
                log.info("HTTP server started on port " + res.result().actualPort());
            } else {
                log.error("HTTP server start fail", res.cause());
            }
        });
        if (http2httpsServer != null) http2httpsServer.listen(res -> {
            if (res.succeeded()) {
                log.info("HTTP to HTTPS server started on port " + res.result().actualPort());
            } else {
                log.error("HTTP to HTTPS server start fail", res.cause());
            }
        });

    }

    private void handleStartCompletion(final AsyncResult<Void> res) {
        if (res.succeeded()) {
            log.info("WebVerticle start success");
        } else {
            log.error("WebVerticle start fail", res.cause());
        }
    }

}
