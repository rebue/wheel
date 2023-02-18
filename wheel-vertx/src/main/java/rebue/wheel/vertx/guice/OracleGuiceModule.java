package rebue.wheel.vertx.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.util.OracleUtils;

import javax.inject.Named;
import javax.inject.Singleton;

@Slf4j
public class OracleGuiceModule extends AbstractModule {

    public OracleGuiceModule() {
        log.info("new OracleGuiceModule");
    }

    @Singleton
    @Provides
    Pool getPool(final Vertx vertx, @Named("config") final JsonObject config) {
        log.info("OracleGuiceModule.getPool");
        return OracleUtils.createPool(vertx, config.getJsonObject("oracle"));
    }

}
