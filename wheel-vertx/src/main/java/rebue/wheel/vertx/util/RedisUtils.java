package rebue.wheel.vertx.util;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;

public class RedisUtils {

    /**
     * Will create a redis client and setup a reconnect handler when there is
     * an exception in the connection.
     */
    public static RedisAPI createRedisClient(final Vertx vertx, final JsonObject config) {
        final JsonObject   redisConfig  = config.getJsonObject("redis");
        final RedisOptions redisOptions = new RedisOptions(redisConfig);
        return RedisAPI.api(Redis.createClient(vertx, redisOptions));
    }

}
