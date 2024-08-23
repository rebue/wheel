package rebue.wheel.vertx.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.common.KafkaClientOptions;
import io.vertx.kafka.client.producer.KafkaProducer;
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
    KafkaClientOptions getKafkaClientOptions(@Named("config") final JsonObject config) {
        log.info("KafkaClientGuiceModule.getKafkaClientOptions");
        final JsonObject kafkaClientConfig = config.getJsonObject("kafka");
        return new KafkaClientOptions(kafkaClientConfig);
    }

    @Singleton
    @Provides
    KafkaProducer getKafkaProducer(Vertx vertx, KafkaClientOptions config) {
        log.info("KafkaClientGuiceModule.getKafkaProducer");
        return KafkaProducer.createShared(vertx, "shared", config);
    }

}
