package rebue.wheel.vertx.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;

import javax.inject.Inject;
import javax.inject.Named;

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
    public void start() {
        log.info("PulsarVerticle start preparing");

        final MessageListener<String> messageListener = (consumer, msg) -> {
            final String data = msg.getValue();
            log.debug("Message received: {}", data);
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
                .messageListener(messageListener);

        final String address = AbstractMainVerticle.EVENT_BUS_DEPLOY_SUCCESS + "::" + this.mainId;
        log.info("PulsarVerticle配置消费EventBus事件-MainVerticle部署成功事件: {}", address);
        this.startConsumer = this.vertx.eventBus().consumer(address, this::handleStart);
        this.startConsumer.completionHandler(this::handleStartCompletion);

        log.info("PulsarVerticle end preparing");
    }

    @SneakyThrows
    @Override
    public void stop() {
        log.info("PulsarVerticle stop");
        if (this._consumer != null) this._consumer.close();
    }

    @SneakyThrows
    private void handleStart(final Message<Void> message) {
        log.info("PulsarVerticle start");
        this.startConsumer.unregister();
        this._consumer = consumerBuilder.subscribe();
    }

    private void handleStartCompletion(final AsyncResult<Void> res) {
        if (res.succeeded()) {
            log.info("PulsarVerticle start success");
        } else {
            log.error("PulsarVerticle start fail", res.cause());
        }
    }

}
