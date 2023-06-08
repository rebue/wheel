package rebue.wheel.vertx.verticle;

import io.grpc.ServerServiceDefinition;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.config.GrpcNettyProperties;

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
    public void start() {
        log.info("GrpcNettyVerticle start preparing");

        final GrpcNettyProperties gRpcNettyProperties = config().mapTo(GrpcNettyProperties.class);
        log.debug("GrpcNettyProperties: {}", gRpcNettyProperties);

        GrpcNettyProperties.ServerProperties serverProperties = gRpcNettyProperties.getServer();

        this.rpcServer = VertxServerBuilder
                .forAddress(this.vertx, serverProperties.getHost(), serverProperties.getPort())
                .addServices(getServices())
                .build();

        final String address = AbstractMainVerticle.EVENT_BUS_DEPLOY_SUCCESS + "::" + this.mainId;
        log.info("GrpcNettyVerticle配置消费EventBus事件-MainVerticle部署成功事件: {}", address);
        this.startConsumer = this.vertx.eventBus().consumer(address, this::handleStart);
        this.startConsumer.completionHandler(this::handleStartCompletion);

        log.info("GrpcNettyVerticle end preparing");
    }

    @Override
    public void stop() {
        log.info("GrpcNettyVerticle stop");
        if (this.rpcServer != null) this.rpcServer.shutdown();
    }

    @SneakyThrows
    private void handleStart(final Message<Void> message) {
        log.info("GrpcNettyVerticle start");
        this.startConsumer.unregister();
        this.rpcServer.start();
    }

    private void handleStartCompletion(final AsyncResult<Void> res) {
        if (res.succeeded()) {
            log.info("GrpcNettyVerticle start success");
        } else {
            log.error("GrpcNettyVerticle start fail", res.cause());
        }
    }

}
