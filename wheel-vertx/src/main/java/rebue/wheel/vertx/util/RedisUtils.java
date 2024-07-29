package rebue.wheel.vertx.util;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisUtils {

    /**
     * Will create a redis client and setup a reconnect handler when there is
     * an exception in the connection.
     */
    public static Redis createRedisClient(final Vertx vertx, final JsonObject redisConfig) {
        final RedisOptions redisOptions = redisConfig == null ? new RedisOptions() : new RedisOptions(redisConfig);
        // if (redisConfig != null) {
        // final JsonArray connectionStrings = redisConfig.getJsonArray("connectionStrings");
        // if (connectionStrings != null) {
        // log.info("Redis连接设置为集群模式: {}", connectionStrings);
        // redisOptions.setType(RedisClientType.CLUSTER);
        // redisOptions.setUseReplicas(RedisReplicas.SHARE);
        // for (final Object connectionString : connectionStrings) {
        // redisOptions.addConnectionString((String) connectionString);
        // }
        // }
        // }
        return Redis.createClient(vertx, redisOptions);
    }

}
