package rebue.wheel.vertx.guice;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.common.KafkaClientOptions;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaGuiceModule extends AbstractModule {

    public KafkaGuiceModule() {
        log.info("new KafkaGuiceModule");
    }

    @Singleton
    @Provides
    KafkaConsumer<String, String> getKafkaConsumer(Vertx vertx, @Named("config") final JsonObject config) {
        log.info("KafkaGuiceModule.getKafkaConsumer");
        final JsonObject kafkaConsumerConfig = config.getJsonObject("kafkaConsumer");
        return KafkaConsumer.create(vertx, new KafkaClientOptions(kafkaConsumerConfig));
    }

}
