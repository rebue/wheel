package rebue.wheel.vertx.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.amqp.AmqpClient;
import io.vertx.amqp.AmqpClientOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AmqpClientGuiceModule extends AbstractModule {

    public AmqpClientGuiceModule() {
        log.info("new AmqpClientGuiceModule");
    }

    @Singleton
    @Provides
    AmqpClient getAmqpClient(Vertx vertx, @Named("config") final JsonObject config) {
        log.info("AmqpClientGuiceModule.getAmqpClient");
        final JsonObject amqpClientConfig = config.getJsonObject("amqpClient");
        return AmqpClient.create(vertx, new AmqpClientOptions(amqpClientConfig));
    }

}
