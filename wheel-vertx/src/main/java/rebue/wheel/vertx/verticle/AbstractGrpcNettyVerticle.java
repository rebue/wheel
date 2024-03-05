package rebue.wheel.vertx.verticle;

import io.grpc.ServerServiceDefinition;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.config.GrpcNettyProperties;

import java.io.IOException;
import java.util.List;

@Slf4j
public abstract class AbstractGrpcNettyVerticle extends AbstractVerticle {

    @Inject
    @Named("mainId")
    private String mainId;

    private VertxServer rpcServer;

    protected abstract List<ServerServiceDefinition> getServices();

    private MessageConsumer<Void> startConsumer;

    @Override
    public void start(Promise<Void> startPromise) {
        log.info("GrpcNettyVerticle start preparing");

        final GrpcNettyProperties gRpcNettyProperties = config().mapTo(GrpcNettyProperties.class);
        log.debug("GrpcNettyProperties: {}", gRpcNettyProperties);

        GrpcNettyProperties.ServerProperties serverProperties = gRpcNettyProperties.getServer();

        this.rpcServer = VertxServerBuilder
                .forAddress(this.vertx, serverProperties.getHost(), serverProperties.getPort())
                .addServices(getServices())
                .build();

        final String address = AbstractMainVerticle.EVENT_BUS_DEPLOY_SUCCESS + "::" + this.mainId;
        log.info("GrpcNettyVerticle注册消费EventBus事件-MainVerticle部署成功事件: {}", address);
        this.startConsumer = this.vertx.eventBus().consumer(address, this::handleStart);
        // 注册完成处理器
        this.startConsumer.completionHandler(res -> {
            log.info("GrpcNettyVerticle end deployed");
            if (res.succeeded()) {
                log.info("GrpcNettyVerticle deployed success");
                startPromise.complete();
            } else {
                log.error("GrpcNettyVerticle deployed fail", res.cause());
                startPromise.fail(res.cause());
            }
        });
    }

    @Override
    public void stop() {
        log.info("GrpcNettyVerticle stop");
        if (this.rpcServer != null) this.rpcServer.shutdown();
    }

    private void handleStart(final Message<Void> message) {
        log.info("GrpcNettyVerticle start");
        this.startConsumer.unregister(res -> {
            try {
                rpcServer.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
