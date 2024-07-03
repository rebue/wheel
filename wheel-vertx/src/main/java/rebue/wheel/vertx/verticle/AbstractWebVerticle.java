package rebue.wheel.vertx.verticle;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import com.google.inject.Injector;

import io.netty.handler.codec.compression.StandardCompressionOptions;
import io.vertx.amqp.AmqpClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.Arguments;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SelfSignedCertificate;
import io.vertx.ext.web.AllowForwardHeaders;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.*;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.api.dic.HttpStatusCodeDic;
import rebue.wheel.vertx.config.WebProperties;
import rebue.wheel.vertx.guice.InjectorVerticle;
import rebue.wheel.vertx.spi.GlobalRouteHandlerFactory;
import rebue.wheel.vertx.spi.VertxWebPluginFactory;
import rebue.wheel.vertx.web.CompressResponseHandler;
import rebue.wheel.vertx.web.PrintSrcIpHandler;

@Slf4j
public abstract class AbstractWebVerticle extends AbstractVerticle implements InjectorVerticle {

    private HttpServer            httpServer;
    private HttpServer            http2httpsServer;

    @Inject
    @Named("mainId")
    private String                mainId;

    @Setter
    protected Injector            injector;

    protected WebProperties       webProperties;
    protected Router              router;

    private MessageConsumer<Void> startConsumer;

    @Override
    public void start(Promise<Void> startPromise) {
        log.info("WebVerticle start deployed");

        webProperties = config().mapTo(WebProperties.class);
        Map<String, Object>     httpServerConfig  = webProperties.getServer();
        final HttpServerOptions httpServerOptions = httpServerConfig == null ? new HttpServerOptions()
                : new HttpServerOptions(JsonObject.mapFrom(httpServerConfig));

        log.info("创建路由");
        router = Router.router(this.vertx);

        log.info("通过SPI加载web插件工厂");
        ServiceLoader<VertxWebPluginFactory> webPluginFactoryServiceLoader = ServiceLoader.load(VertxWebPluginFactory.class);
        log.info("初始化web插件工厂");
        webPluginFactoryServiceLoader.forEach(factory -> {
            log.info("初始化web插件工厂: {}", factory.name());
            factory.init(vertx, injector, webProperties.getGlobalRouteHandlers().get(factory.name()));
        });

        AllowForwardHeaders allowForwardHeaders = AllowForwardHeaders.valueOf(webProperties.getAllowForward());
        log.info("设置allow forward: {}", allowForwardHeaders);
        router.allowForward(allowForwardHeaders);

        // 全局route
        final Route globalRoute = router.route();

        // 支持压缩与解压缩算法
        Object      compressors = httpServerConfig == null ? null : httpServerConfig.get("compressors");
        if (compressors != null) {
            log.info("开启压缩与解压缩");
            httpServerOptions.setCompressionSupported(true);
            httpServerOptions.setDecompressionSupported(true);
            @SuppressWarnings("unchecked")
            List<String> list = (List<String>) compressors;
            for (String compressor : list) {
                log.info("add compressor: {}", compressor);
                switch (compressor) {
                case "brotli" -> httpServerOptions.addCompressor(StandardCompressionOptions.brotli());
                case "deflate" -> httpServerOptions.addCompressor(StandardCompressionOptions.deflate());
                case "gzip" -> httpServerOptions.addCompressor(StandardCompressionOptions.gzip());
                case "snappy" -> httpServerOptions.addCompressor(StandardCompressionOptions.snappy());
                case "zstd" -> httpServerOptions.addCompressor(StandardCompressionOptions.zstd());
                }
            }

            globalRoute.handler(new CompressResponseHandler());
        }

        // 全局返回响应时间(写入x-response-time到响应头)
        if (webProperties.getReturnResponseTime()) {
            log.info("开启返回响应时间");
            globalRoute.handler(ResponseTimeHandler.create());
        }
        // 超时时间
        final Long timeout = webProperties.getTimeout();
        if (timeout != null && timeout != 0) {
            final Integer timeoutErrorCode = webProperties.getTimeoutErrorCode();
            final int     errorCode        = timeoutErrorCode != null ? timeoutErrorCode : 503;
            log.info("开启超时{}毫秒未响应返回错误状态码{}", timeout, errorCode);
            globalRoute.handler(TimeoutHandler.create(timeout, errorCode));
        }
        // 记录日志
        if (webProperties.getIsLogging()) {
            log.info("开启日志记录");
            globalRoute.handler(LoggerHandler.create(webProperties.getLoggerFormat()));
        }
        // 是否打印来源的IP
        if (webProperties.getPrintSrcIp()) {
            log.info("开启打印来源的IP");
            globalRoute.handler(new PrintSrcIpHandler());
        }
        // 自动响应内容类型(处理器会通过 getAcceptableContentType 方法来选择适当的内容类型)
        if (webProperties.getIsAutoResponseContentType()) {
            log.info("开启自动响应内容类型");
            globalRoute.handler(ResponseContentTypeHandler.create());
        }
        // CORS
        if (webProperties.getIsCors()) {
            log.info("开启CORS");
            globalRoute.handler(CorsHandler.create());
        }

        log.info("添加子类中的全局路由处理器");
        addGlobalRouteHandler(globalRoute);
        log.info("通过SPI加载全局路由处理器");
        ServiceLoader<GlobalRouteHandlerFactory> globalRouteHandlerServiceLoader = ServiceLoader.load(GlobalRouteHandlerFactory.class);
        log.info("添加全局路由前置处理器");
        globalRouteHandlerServiceLoader.forEach(factory -> {
            Handler<RoutingContext> preHandler = factory.createPreHandler();
            if (preHandler != null) {
                log.info("添加全局路由前置处理器: {}", factory.name());
                globalRoute.handler(preHandler);
            }
        });

        log.info("配置路由器");
        configRouter();

        log.info("添加全局路由后置处理器");
        globalRouteHandlerServiceLoader.forEach(factory -> {
            Handler<RoutingContext> postHandler = factory.createPostHandler();
            if (postHandler != null) {
                log.info("添加全局路由后置处理器: {}", factory.name());
                globalRoute.handler(postHandler);
            }
        });

        log.info("添加全局路由错误处理");
        globalRoute.failureHandler(ErrorHandler.create(this.vertx));

        // 是否实现自签名证书
        if (webProperties.getSelfSignedCertificate()) {
            log.info("实现自签名证书");
            SelfSignedCertificate certificate = SelfSignedCertificate.create();
            httpServerOptions
                    .setSsl(true)
                    .setKeyCertOptions(certificate.keyCertOptions())
                    .setTrustOptions(certificate.trustOptions());
        }

        this.httpServer = this.vertx.createHttpServer(httpServerOptions).requestHandler(router);

        Map<String, Object> http2https = webProperties.getHttp2https();
        if (http2https != null) {
            final HttpServerOptions http2httpsServerOptions = new HttpServerOptions(
                    JsonObject.mapFrom(webProperties.getHttp2https()));
            int                     http2httpsPort          = http2httpsServerOptions.getPort();
            int                     httpsPort               = httpServerOptions.getPort();
            Arguments.require(http2httpsPort != 0, "web.config.http2https.port不能为null或0");
            Arguments.require(httpsPort != 0, "web.config.server.port不能为null或0");

            this.http2httpsServer = this.vertx.createHttpServer(http2httpsServerOptions)
                    .requestHandler(req -> req.response()
                            .setStatusCode(301)
                            .putHeader("Location", req.absoluteURI()
                                    .replace("http", "https")
                                    .replace(":" + http2httpsPort, ":" + httpsPort))
                            .end());
        }

        final String address = AbstractMainVerticle.EVENT_BUS_DEPLOY_SUCCESS + "::" + this.mainId;
        log.info("WebVerticle注册消费EventBus事件-MainVerticle部署成功事件: {}", address);
        this.startConsumer = this.vertx.eventBus().consumer(address, this::handleStart);
        // 注册完成处理器
        this.startConsumer.completionHandler(res -> {
            log.info("WebVerticle end deployed");
            if (res.succeeded()) {
                log.info("WebVerticle deployed success");
                startPromise.complete();
            } else {
                log.error("WebVerticle deployed fail", res.cause());
                startPromise.fail(res.cause());
            }
        });
    }

    /**
     * 添加全局路由处理器
     *
     * @param globalRoute 全局路由
     */
    protected void addGlobalRouteHandler(Route globalRoute) {
        log.info("未重写addGlobalRouteHandler方法: {}", globalRoute.getName());
    }

    @Override
    public void stop() {
        log.info("WebVerticle stop");
        if (http2httpsServer != null)
            http2httpsServer.close();
        this.httpServer.close();
    }

    /**
     * 配置路由
     */
    protected abstract void configRouter();

    private void handleStart(final Message<Void> message) {
        log.info("WebVerticle start");
        this.startConsumer.unregister(result -> {
            this.httpServer.listen(res -> {
                if (res.succeeded()) {
                    log.info("HTTP server started on port {}", res.result().actualPort());
                } else {
                    log.error("HTTP server start fail", res.cause());
                }
                // 是否开启动态路由
                if (webProperties.getDynamicRoute().getEnabled()) {
                    log.info("开启动态路由");
                    switch (webProperties.getDynamicRoute().getMqType().toLowerCase()) {
                    case "rabbitmq" -> {
                        AmqpClient amqpClient = injector.getInstance(AmqpClient.class);
                        amqpClient.createReceiver(webProperties.getDynamicRoute().getMqName())
                                .compose(receiver -> {
                                    log.info("订阅动态路由刷新队列成功-rabbitmq");
                                    // 刷新动态路由
                                    configRouter();
                                    return Future.succeededFuture();
                                }).recover(err -> {
                                    log.error("订阅动态路由刷新队列失败", err);
                                    return Future.failedFuture(err);
                                });
                    }
                    case "kafka" -> {
                        // noinspection unchecked
                        KafkaConsumer<String, String> kafkaConsumer = injector.getInstance(KafkaConsumer.class);
                        kafkaConsumer.subscribe(webProperties.getDynamicRoute().getMqName())
                                .compose(v -> {
                                    log.info("订阅动态路由刷新队列成功-kafka");
                                    // 刷新动态路由
                                    configRouter();
                                    return Future.succeededFuture();
                                }).recover(err -> {
                                    log.error("订阅动态路由刷新队列失败", err);
                                    return Future.failedFuture(err);
                                });
                    }
                    }
                }
            });
            if (http2httpsServer != null)
                http2httpsServer.listen(res -> {
                    if (res.succeeded()) {
                        log.info("HTTP to HTTPS server started on port {}", res.result().actualPort());
                    } else {
                        log.error("HTTP to HTTPS server start fail", res.cause());
                    }
                });
        });
    }

}
