package rebue.wheel.vertx.verticle;

import io.grpc.ServerServiceDefinition;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.config.GrpcNettyProperties;

import javax.inject.Inject;
import javax.inject.Named;
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

        rpcServer = VertxServerBuilder
                .forAddress(this.vertx, serverProperties.getHost(), serverProperties.getPort())
                .addServices(getServices())
                .build();

        log.info("GrpcNettyVerticle配置消费EventBus事件-MainVerticle部署成功事件");
        final String address = AbstractMainVerticle.EVENT_BUS_DEPLOY_SUCCESS + "::" + this.mainId;
        this.startConsumer = this.vertx.eventBus().consumer(address, this::handleStart);
        this.startConsumer.completionHandler(this::handleStartCompletion);

        log.info("GrpcNettyVerticle end preparing");
    }

    @Override
    public void stop() {
        log.info("GrpcNettyVerticle stop");
    }

    @SneakyThrows
    private void handleStart(final Message<Void> message) {
        log.info("GrpcNettyVerticle start");
        this.startConsumer.unregister();
        rpcServer.start();
    }

    private void handleStartCompletion(final AsyncResult<Void> res) {
        if (res.succeeded()) {
            log.info("GrpcNettyVerticle start success");
        } else {
            log.error("GrpcNettyVerticle start fail", res.cause());
        }
    }

}
