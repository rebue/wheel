package rebue.wheel.vertx.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import rebue.wheel.vertx.config.PulsarClientProperties;

import javax.inject.Named;
import javax.inject.Singleton;

@Slf4j
public class PulsarGuiceModule extends AbstractModule {

    @Singleton
    @Provides
    PulsarClient getPulsarClient(@Named("config") final JsonObject config) {
        log.info("PulsarGuiceModule.getPulsarClient");

        JsonObject pulsarClientPropertiesJsonObject = config.getJsonObject("pulsarClient");

        PulsarClientProperties pulsarClientProperties = pulsarClientPropertiesJsonObject == null
                ? new PulsarClientProperties()
                : pulsarClientPropertiesJsonObject.mapTo(PulsarClientProperties.class);

        try {
            return PulsarClient.builder()
                    .serviceUrl(pulsarClientProperties.getServiceUrl())
                    .build();
        } catch (PulsarClientException e) {
            log.error("构建PulsarClient出现异常", e);
            throw new RuntimeException(e);
        }

    }

}
