package rebue.wheel.vertx.guice;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import rebue.wheel.vertx.util.OracleUtils;

public class OracleGuiceModule extends AbstractModule {

    // static {
    // // log4j支持jul
    // // FIXME 下面这句会导致vertx的服务启动时报“Cannot bind Unsafe.defineAnonymousClass---com.google.inject.internal.aop.UnsafeClassDefiner.log4j”，但是debug级别的，不知道会有什么问题
    // Log4jBridgeHandler.install(true, "log4j", true);
    // }

    @Singleton
    @Provides
    Pool getOraclePool(final Vertx vertx, @Named("config") final JsonObject config) {
        return OracleUtils.createClient(vertx, config.getJsonObject("oracle"));
    }

}
