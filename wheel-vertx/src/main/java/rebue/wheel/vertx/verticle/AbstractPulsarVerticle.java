package rebue.wheel.vertx.verticle;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractPulsarVerticle extends AbstractVerticle {

    @Inject
    @Named("mainId")
    private String                mainId;

    @Inject
    private PulsarClient          pulsarClient;

    private MessageConsumer<Void> startConsumer;

    protected abstract String getTopic();

    protected abstract String getSubscriptionName();

    protected abstract Future<Boolean> receivedData(String data);

    @Override
    public void start() throws Exception {
        log.info("PulsarVerticle start");

        log.info("配置消费EventBus事件-MainVerticle部署成功事件");
        final String address = AbstractMainVerticle.EVENT_BUS_DEPLOY_SUCCESS + "::" + this.mainId;
        log.info("MainVerticle.EVENT_BUS_DEPLOY_SUCCESS address is " + address);
        this.startConsumer = this.vertx.eventBus()
                .consumer(address, this::handleStart);
        this.startConsumer.completionHandler(this::handleStartCompletion);

        log.info("PulsarVerticle Started");
    }

    @Override
    public void stop() throws Exception {
        log.info("PulsarVerticle stop");
    }

    private void handleStart(final Message<Void> message) {
        this.startConsumer.unregister();

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

        try {
            pulsarClient.newConsumer(Schema.STRING)
                    .topic(getTopic())
                    .subscriptionName(getSubscriptionName())
                    .messageListener(messageListener)
                    .subscribe();
        } catch (final PulsarClientException e) {
            log.error("创建消息消费者并订阅出现异常", e);
            throw new RuntimeException(e);
        }
    }

    private void handleStartCompletion(final AsyncResult<Void> res) {
        if (res.succeeded()) {
            log.info("Event Bus register success: consumer.start");
        } else {
            log.error("Event Bus register fail: consumer.start", res.cause());
        }
    }

}
