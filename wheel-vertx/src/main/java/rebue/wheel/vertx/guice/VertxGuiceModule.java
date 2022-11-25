package rebue.wheel.vertx.guice;

import javax.inject.Named;
import javax.inject.Singleton;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxGuiceModule extends AbstractModule {

    protected Vertx          vertx;
    private final JsonObject config;

    public VertxGuiceModule(final Vertx vertx, final JsonObject config) {
        log.info("new VertxGuiceModule");
        this.vertx  = vertx;
        this.config = config;
    }

    @Provides
    @Singleton
    Vertx getVertx() {
        log.info("VertxGuiceModule.getVertx");
        return this.vertx;
    }

    @Provides
    @Singleton
    EventBus getEventBus() {
        log.info("VertxGuiceModule.getEventBus");
        return this.vertx.eventBus();
    }

    @Provides
    @Singleton
    @Named("config")
    JsonObject getConfig() {
        log.info("VertxGuiceModule.getConfig");
        return this.config;
    }

    @Provides
    @Singleton
    @Named("deliveryOptions")
    JsonObject getDeliveryOptions(@Named("config") final JsonObject config) {
        log.info("VertxGuiceModule.getDeliveryOptions");
        final JsonObject deliveryConfig = config.getJsonObject("delivery");
        return deliveryConfig == null ? new JsonObject() : deliveryConfig;
    }

    @Provides
    @Singleton
    @Named("mainId")
    String getMainId() {
        log.info("VertxGuiceModule.getMainId");
        return NanoIdUtils.randomNanoId();
    }

}
