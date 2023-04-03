package rebue.wheel.vertx.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Named;
import javax.inject.Singleton;

@Slf4j
public class WebClientGuiceModule extends AbstractModule {

    public WebClientGuiceModule() {
        log.info("new WebClientGuiceModule");
    }

    @Singleton
    @Provides
    WebClient getWebClient(final Vertx vertx, @Named("config") final JsonObject config) {
        log.info("WebClientGuiceModule.getWebClient");
        final JsonObject webClientConfig = config.getJsonObject("webClient");
        if (webClientConfig == null) {
            return WebClient.create(vertx);
        }
        return WebClient.create(vertx, new WebClientOptions(webClientConfig));
    }

}
