package rebue.wheel.vertx.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SockjsGuiceModule extends AbstractModule {

    public SockjsGuiceModule() {
        log.info("new SockjsGuiceModule");
    }

    @Singleton
    @Provides
    SockJSHandler getSockjs(final Vertx vertx, @Named("config") final JsonObject config) {
        log.info("SockjsGuiceModule.getSockjs");
        final JsonObject     optionsJsonObject = config.getJsonObject("sockjs");
        SockJSHandlerOptions options           = optionsJsonObject == null
                ? new SockJSHandlerOptions()
                : new SockJSHandlerOptions(optionsJsonObject);
        return SockJSHandler.create(vertx, options);
    }

}
