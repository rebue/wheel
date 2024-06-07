package rebue.wheel.vertx.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.common.KafkaClientOptions;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaClientGuiceModule extends AbstractModule {

    public KafkaClientGuiceModule() {
        log.info("new KafkaClientGuiceModule");
    }

    @Singleton
    @Provides
    KafkaConsumer<String, String> getKafkaConsumer(Vertx vertx, @Named("config") final JsonObject config) {
        log.info("KafkaClientGuiceModule.getKafkaConsumer");
        final JsonObject kafkaConsumerConfig = config.getJsonObject("kafkaConsumer");
        return KafkaConsumer.create(vertx, new KafkaClientOptions(kafkaConsumerConfig));
    }

}
