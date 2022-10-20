package rebue.wheel.vertx.guice;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.logging.log4j.jul.Log4jBridgeHandler;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.util.OracleUtils;

@Slf4j
public class OracleGuiceModule extends AbstractModule {

    static {
        // log4j支持jul
        // FIXME 下面这句会导致vertx的服务启动时报“Cannot bind Unsafe.defineAnonymousClass---com.google.inject.internal.aop.UnsafeClassDefiner.log4j”，但是debug级别的，不知道会有什么问题
        Log4jBridgeHandler.install(true, "log4j", true);
    }

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
