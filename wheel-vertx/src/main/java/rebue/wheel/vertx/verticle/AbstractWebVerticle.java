package rebue.wheel.vertx.verticle;

import javax.inject.Inject;
import javax.inject.Named;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;
import io.vertx.ext.web.handler.TimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.config.WebProperties;

@Slf4j
public abstract class AbstractWebVerticle extends AbstractVerticle {
    private WebProperties         webProperties;
    private HttpServer            httpServer;

    @Inject
    @Named("mainId")
    private String                mainId;
    private MessageConsumer<Void> startConsumer;

    @Override
    public void start() {
        this.webProperties = config().mapTo(WebProperties.class);
        final HttpServerOptions httpServerOptions = this.webProperties.getServer() == null ? new HttpServerOptions()
                : new HttpServerOptions(JsonObject.mapFrom(this.webProperties.getServer()));

        log.info("创建路由");
        final Router router      = Router.router(this.vertx);
        // 全局route
        final Route  globalRoute = router.route();
        // 全局响应处理(处理器会通过 getAcceptableContentType 方法来选择适当的内容类型)
        globalRoute.handler(ResponseContentTypeHandler.create());
        // 全局返回响应时间
        if (this.webProperties.getIsResponseTime()) {
            log.info("开启返回响应时间");
            globalRoute.handler(ResponseTimeHandler.create());
        }
        // 超时时间
        final Long timeout = this.webProperties.getTimeout();
        if (timeout != null && timeout != 0) {
            final Integer timeoutErrorCode = this.webProperties.getTimeoutErrorCode();
            final int     errorCode        = timeoutErrorCode != null ? timeoutErrorCode : 503;
            log.info("开启超时返回错误状态码{}", errorCode);
            globalRoute.handler(TimeoutHandler.create(timeout, errorCode));
        }
        // 记录日志
        if (this.webProperties.getIsLogging()) {
            log.info("开启Web日志记录");
            globalRoute.handler(LoggerHandler.create(this.webProperties.getLoggerFormat()));
        }
        // CORS
        if (this.webProperties.getIsCors()) {
            log.info("开启CORS");
            globalRoute.handler(CorsHandler.create("*")
                    .allowedMethod(HttpMethod.GET)
                    .allowedMethod(HttpMethod.POST)
                    .allowedMethod(HttpMethod.PUT)
                    .allowedMethod(HttpMethod.DELETE)
                    .allowedMethod(HttpMethod.PATCH)
                    .allowedMethod(HttpMethod.OPTIONS));
        }
        // 全局路由错误处理
        final ErrorHandler errorHandler = ErrorHandler.create(this.vertx);
        globalRoute.failureHandler(ctx -> {
            log.error("全局路由错误处理: {}", ctx.statusCode());
            errorHandler.handle(ctx);
        });

        log.info("配置路由器");
        configRouter(router);

        this.httpServer = this.vertx.createHttpServer(httpServerOptions).requestHandler(router);

        log.info("配置消费EventBus事件-MainVerticle部署成功事件");
        final String address = AbstractMainVerticle.EVENT_BUS_DEPLOY_SUCCESS + "::" + this.mainId;
        log.info("MainVerticle.EVENT_BUS_DEPLOY_SUCCESS address is " + address);
        this.startConsumer = this.vertx.eventBus()
                .consumer(address, this::handleStart);
        this.startConsumer.completionHandler(this::handleStartCompletion);

        log.info("WebVerticle Started");
    }

    /**
     * 配置路由
     *
     * @param router 路由器
     */
    protected abstract void configRouter(Router router);

    private void handleStart(final Message<Void> message) {
        this.startConsumer.unregister();
        this.httpServer.listen(res -> {
            if (res.succeeded()) {
                log.info("HTTP server started on port " + res.result().actualPort());
            } else {
                log.error("HTTP server start fail", res.cause());
            }
        });
    }

    private void handleStartCompletion(final AsyncResult<Void> res) {
        if (res.succeeded()) {
            log.info("Event Bus register success: web.start");
        } else {
            log.error("Event Bus register fail: web.start", res.cause());
        }
    }

    @Override
    public void stop() throws Exception {
        log.info("WebVerticle stop");
    }

}
