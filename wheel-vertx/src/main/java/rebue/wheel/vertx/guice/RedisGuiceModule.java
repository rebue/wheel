package rebue.wheel.vertx.guice;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.RedisAPI;
import rebue.wheel.vertx.util.RedisUtils;

public class RedisGuiceModule extends AbstractModule {

    @Singleton
    @Provides
    RedisAPI getRedisAPI(final Vertx vertx, final JsonObject config) {
        return RedisUtils.createRedisClient(vertx, config.getJsonObject("redis"));
    }

}
