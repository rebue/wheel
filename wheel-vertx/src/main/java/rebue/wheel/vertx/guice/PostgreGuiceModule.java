package rebue.wheel.vertx.guice;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import rebue.wheel.vertx.util.PostgreUtils;

public class PostgreGuiceModule extends AbstractModule {

    @Singleton
    @Provides
    Pool getSqlClient(final Vertx vertx, @Named("config") final JsonObject config) {
        return PostgreUtils.createPool(vertx, config.getJsonObject("postgre"));
    }

}
