package rebue.wheel.vertx.guice;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.logging.log4j.jul.Log4jBridgeHandler;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import rebue.wheel.vertx.util.OracleUtils;

public class OracleGuiceModule extends AbstractModule {

    static {
        // log4j支持jul
        Log4jBridgeHandler.install(true, "log4j", true);
    }

    @Singleton
    @Provides
    Pool getOraclePool(final Vertx vertx, @Named("config") final JsonObject config) {
        return OracleUtils.createClient(vertx, config.getJsonObject("oracle"));
    }

}
