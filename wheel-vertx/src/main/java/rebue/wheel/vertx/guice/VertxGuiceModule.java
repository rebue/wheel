package rebue.wheel.vertx.guice;

import javax.inject.Named;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

public class VertxGuiceModule extends AbstractModule {

    protected Vertx    vertx;
    private JsonObject config;

    public VertxGuiceModule(final Vertx vertx, final JsonObject config) {
        this.vertx  = vertx;
        this.config = config;
    }

    @Provides
    Vertx getVertx() {
        return this.vertx;
    }

    @Provides
    EventBus getEventBus() {
        return this.vertx.eventBus();
    }

    @Provides
    @Named("config")
    JsonObject getConfig() {
        return this.config;
    }

}
