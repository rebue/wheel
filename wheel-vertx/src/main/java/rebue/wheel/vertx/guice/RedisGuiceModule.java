package rebue.wheel.vertx.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.util.RedisUtils;

@Slf4j
public class RedisGuiceModule extends AbstractModule {

    public RedisGuiceModule() {
        log.info("new RedisGuiceModule");
    }

    @Singleton
    @Provides
    Redis getRedis(final Vertx vertx, @Named("config") final JsonObject config) {
        log.info("RedisGuiceModule.getRedis");
        return RedisUtils.createRedisClient(vertx, config.getJsonObject("redis"));
    }

    @Singleton
    @Provides
    RedisAPI getRedisApi(Redis redis) {
        log.info("RedisGuiceModule.getRedisApi");
        return RedisAPI.api(redis);
    }
}
