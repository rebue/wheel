package rebue.wheel.vertx.guice;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.util.PostgreUtils;

@Slf4j
public class PostgreGuiceModule extends AbstractModule {

    public PostgreGuiceModule() {
        log.info("new PostgreGuiceModule");
    }

    @Singleton
    @Provides
    Pool getPool(final Vertx vertx, @Named("config") final JsonObject config) {
        log.info("PostgreGuiceModule.getPool");
        return PostgreUtils.createPool(vertx, config.getJsonObject("postgre"));
    }

}
