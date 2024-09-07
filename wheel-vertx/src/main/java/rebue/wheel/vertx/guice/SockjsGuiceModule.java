package rebue.wheel.vertx.guice;

import java.util.Optional;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
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
    SockJSHandler getSockjsHandle(final Vertx vertx, @Named("config") final JsonObject config) {
        log.info("SockjsGuiceModule.getSockjsHandle");
        SockJSHandlerOptions sockjsHandlerOptions = Optional
                .ofNullable(config.getJsonObject("sockjs"))
                .map(options -> options.getJsonObject("sockjsHandlerOptions"))
                .map(SockJSHandlerOptions::new)
                .orElse(new SockJSHandlerOptions());
        return SockJSHandler.create(vertx, sockjsHandlerOptions);
    }

    @Singleton
    @Provides
    SockJSBridgeOptions getSockjsBridgeOptions(@Named("config") final JsonObject config) {
        log.info("SockjsGuiceModule.getSockjs");
        return Optional
                .ofNullable(config.getJsonObject("sockjs"))
                .map(options -> options.getJsonObject("sockjsBridgeOptions"))
                .map(SockJSBridgeOptions::new)
                .orElse(new SockJSBridgeOptions());
    }

}
