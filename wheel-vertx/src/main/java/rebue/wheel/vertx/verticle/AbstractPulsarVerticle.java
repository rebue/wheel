package rebue.wheel.vertx.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;

@Slf4j
public abstract class AbstractPulsarVerticle extends AbstractVerticle {

    @Inject
    @Named("mainId")
    private String mainId;

    @Inject
    private PulsarClient pulsarClient;

    private MessageConsumer<Void>   startConsumer;
    private ConsumerBuilder<String> consumerBuilder;
    private Consumer<String>        _consumer;

    protected abstract String getTopic();

    protected abstract String getSubscriptionName();

    protected abstract Future<Boolean> receivedData(String data);

    @Override
    public void start(Promise<Void> startPromise) {
        log.info("PulsarVerticle start deployed");

        final MessageListener<String> messageListener = (consumer, msg) -> {
            final String data = msg.getValue();
            log.debug("接收到消息: {}", data);
            receivedData(data).compose(bool -> {
                if (bool) {
                    try {
                        consumer.acknowledge(msg);
                    } catch (final PulsarClientException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    consumer.negativeAcknowledge(msg);
                }
                return Future.succeededFuture();
            }).recover(err -> {
                log.error("接收消息处理数据出现异常", err);
                consumer.negativeAcknowledge(msg);
                return Future.succeededFuture();
            });
        };

        consumerBuilder = pulsarClient.newConsumer(Schema.STRING)
                .topic(getTopic())
                .subscriptionName(getSubscriptionName())
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(messageListener);

        final String address = AbstractMainVerticle.EVENT_BUS_DEPLOY_SUCCESS + "::" + this.mainId;
        log.info("PulsarVerticle注册消费EventBus事件-MainVerticle部署成功事件: {}", address);
        this.startConsumer = this.vertx.eventBus().consumer(address, this::handleStart);
        // 注册完成处理器
        this.startConsumer.completionHandler(res -> {
            log.info("PulsarVerticle end deployed");
            if (res.succeeded()) {
                log.info("PulsarVerticle deployed success");
                startPromise.complete();
            } else {
                log.error("PulsarVerticle deployed fail", res.cause());
                startPromise.fail(res.cause());
            }
        });
    }

    @SneakyThrows
    @Override
    public void stop() {
        log.info("PulsarVerticle stop");
        if (this._consumer != null) this._consumer.close();
    }

    private void handleStart(final Message<Void> message) {
        log.info("PulsarVerticle start");
        this.startConsumer.unregister(res -> {
            try {
                this._consumer = consumerBuilder.subscribe();
            } catch (PulsarClientException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
