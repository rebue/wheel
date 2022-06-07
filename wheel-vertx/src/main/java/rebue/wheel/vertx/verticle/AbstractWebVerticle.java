package rebue.wheel.vertx.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.config.WebProperties;

@Slf4j
public abstract class AbstractWebVerticle extends AbstractVerticle {
    private WebProperties webProperties;
    private HttpServer    httpServer;

    @Override
    public void start() {
        this.webProperties = config().mapTo(WebProperties.class);
        final HttpServerOptions httpServerOptions = this.webProperties.getServer() == null ? new HttpServerOptions()
                : new HttpServerOptions(JsonObject.mapFrom(this.webProperties.getServer()));

        log.info("创建路由");
        final Router router      = Router.router(this.vertx);
        // 全局route
        final Route  globalRoute = router.route();
        // 记录日志
        if (this.webProperties.getIsLogging()) {
            log.info("开启日志记录");
            globalRoute.handler(LoggerHandler.create(this.webProperties.getLoggerFormat()));
        }
        // CORS
        if (this.webProperties.getIsCors()) {
            log.info("开启CORS");
            globalRoute.handler(CorsHandler.create("*").allowedMethod(HttpMethod.GET));
        }

        log.info("配置路由");
        configRouter(router);

        this.httpServer = this.vertx.createHttpServer(httpServerOptions).requestHandler(router);

        this.vertx.eventBus()
                .consumer(AbstractMainVerticle.EVENT_BUS_DEPLOY_SUCCESS, this::handleStart)
                .completionHandler(this::handleStartCompletion);

        log.info("WebVerticle Started");
    }

    /**
     * 配置路由
     *
     * @param router 路由器
     */
    protected abstract void configRouter(Router router);

    private void handleStart(final Message<Void> message) {
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

}
