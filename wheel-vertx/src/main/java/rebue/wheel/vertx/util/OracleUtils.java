package rebue.wheel.vertx.util;

import java.util.Map.Entry;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.oracleclient.OracleBuilder;
import io.vertx.oracleclient.OracleConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import rebue.wheel.vertx.config.OracleConfig;

public class OracleUtils {
    /**
     * 创建客户端
     */
    public static Pool createPool(final Vertx vertx, final JsonObject oracleConfig) {
        final JsonObject           config         = oracleConfig.getJsonObject(OracleConfig.CONNECT_PREFIX);
        final OracleConnectOptions connectOptions = config.mapTo(OracleConnectOptions.class);
        final JsonObject           properties     = config.getJsonObject("properties");
        if (properties != null && !properties.isEmpty()) {
            for (final Entry<String, Object> entry : properties) {
                connectOptions.addProperty(entry.getKey(), entry.getValue().toString());
            }
        }
        final PoolOptions poolOptions = oracleConfig.getJsonObject(OracleConfig.POOL_PREFIX).mapTo(PoolOptions.class);
        return OracleBuilder.pool().using(vertx).with(poolOptions).connectingTo(connectOptions).build();
    }
}
