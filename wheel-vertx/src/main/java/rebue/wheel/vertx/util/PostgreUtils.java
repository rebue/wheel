package rebue.wheel.vertx.util;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import rebue.wheel.vertx.config.PostgreConfig;

import java.util.Map.Entry;

public class PostgreUtils {
    /**
     * 创建客户端
     */
    public static Pool createPool(final Vertx vertx, final JsonObject postgreConfig) {
        final JsonObject       config         = postgreConfig.getJsonObject(PostgreConfig.CONNECT_PREFIX);
        final PgConnectOptions connectOptions = config.mapTo(PgConnectOptions.class);
        final JsonObject       properties     = config.getJsonObject("properties");
        if (properties != null && !properties.isEmpty()) {
            for (final Entry<String, Object> entry : properties) {
                connectOptions.addProperty(entry.getKey(), entry.getValue().toString());
            }
        }
        final PoolOptions poolOptions = postgreConfig.getJsonObject(PostgreConfig.POOL_PREFIX).mapTo(PoolOptions.class);
        return PgPool.pool(vertx, connectOptions, poolOptions.setShared(true));
    }
}
