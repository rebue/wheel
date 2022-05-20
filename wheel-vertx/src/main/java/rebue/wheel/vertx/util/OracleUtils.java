package rebue.wheel.vertx.util;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.oracleclient.OracleConnectOptions;
import io.vertx.oracleclient.OraclePool;
import io.vertx.sqlclient.PoolOptions;
import rebue.wheel.vertx.config.OracleConfig;

public class OracleUtils {
    /**
     * 创建客户端
     */
    public static OraclePool createClient(final Vertx vertx, final JsonObject oracleConfig) {
        final OracleConnectOptions connectOptions = oracleConfig.getJsonObject(OracleConfig.CONNECT_PREFIX).mapTo(OracleConnectOptions.class);
        final PoolOptions          poolOptions    = oracleConfig.getJsonObject(OracleConfig.POOL_PREFIX).mapTo(PoolOptions.class);
        return OraclePool.pool(vertx, connectOptions, poolOptions.setShared(true));
    }
}
