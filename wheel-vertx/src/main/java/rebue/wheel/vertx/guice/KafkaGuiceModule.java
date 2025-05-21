package rebue.wheel.vertx.guice;

import javax.annotation.Nullable;

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
public class KafkaGuiceModule extends AbstractModule {

	public KafkaGuiceModule() {
		log.info("new KafkaGuiceModule");
	}

	@Singleton
	@Provides
	KafkaClientOptions getKafkaClientOptions(@Named("config") final JsonObject config) {
		log.info("KafkaClientGuiceModule.getKafkaClientOptions");
		final JsonObject kafkaClientConfig = config.getJsonObject("kafka");
		if (kafkaClientConfig == null) {
			return null;
		}
		return new KafkaClientOptions(kafkaClientConfig);
	}

	@Singleton
	@Provides
	<K, V> KafkaProducer<K, V> getKafkaProducer(Vertx vertx, @Nullable KafkaClientOptions kafkaClientConfig) {
		log.info("KafkaClientGuiceModule.getKafkaProducer");
		if (kafkaClientConfig == null) {
			return null;
		}
		return KafkaProducer.createShared(vertx, "shared", kafkaClientConfig);
	}

}
