package rebue.wheel.vertx.util;

import java.util.Map.Entry;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import rebue.wheel.vertx.config.PostgreConfig;

public class PostgreUtils {
    /**
     * 创建客户端
     */
    public static Pool createPool(final Vertx vertx, final JsonObject oracleConfig) {
        final JsonObject       config         = oracleConfig.getJsonObject(PostgreConfig.CONNECT_PREFIX);
        final PgConnectOptions connectOptions = config.mapTo(PgConnectOptions.class);
        final JsonObject       properties     = config.getJsonObject("properties");
        if (properties != null && !properties.isEmpty()) {
            for (final Entry<String, Object> entry : properties) {
                connectOptions.addProperty(entry.getKey(), entry.getValue().toString());
            }
        }
        final PoolOptions poolOptions = oracleConfig.getJsonObject(PostgreConfig.POOL_PREFIX).mapTo(PoolOptions.class);
        return PgPool.pool(connectOptions, poolOptions.setShared(true));
    }
}
