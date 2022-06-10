package rebue.wheel.vertx.guice;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

public class WebClientGuiceModule extends AbstractModule {

    @Singleton
    @Provides
    WebClient getWebClient(final Vertx vertx, @Named("config") final JsonObject config) {
        return WebClient.create(vertx, new WebClientOptions(config.getJsonObject("webClient")));
    }

}
