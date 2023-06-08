package rebue.wheel.vertx.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import rebue.wheel.vertx.config.PulsarClientProperties;

@Slf4j
public class PulsarGuiceModule extends AbstractModule {

    public PulsarGuiceModule() {
        log.info("new PulsarGuiceModule");
    }

    @Singleton
    @Provides
    PulsarClient getPulsarClient(@Named("config") final JsonObject config) {
        log.info("PulsarGuiceModule.getPulsarClient");

        final JsonObject pulsarClientPropertiesJsonObject = config.getJsonObject("pulsar");

        final PulsarClientProperties pulsarClientProperties = pulsarClientPropertiesJsonObject == null
                ? new PulsarClientProperties()
                : pulsarClientPropertiesJsonObject.mapTo(PulsarClientProperties.class);

        try {
            return PulsarClient.builder()
                    .serviceUrl(pulsarClientProperties.getServiceUrl())
                    .build();
        } catch (final PulsarClientException e) {
            log.error("构建PulsarClient出现异常", e);
            throw new RuntimeException(e);
        }

    }

}
