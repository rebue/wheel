package rebue.wheel.vertx.guice;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.RedisAPI;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.core.idworker.IdWorker3;

@Slf4j
public class IdWorkerGuiceModule extends AbstractModule {

    /**
     * idworker的Key的前缀
     * 后面跟配置的字符串拼接成Key
     * Value为自增的值
     */
    private static final String REDIS_KEY_ID_WORKER_PREFIX = "rebue.wheel.vertx.id-worker::";

    @Singleton
    @Provides
    IdWorker3 getIdWorker(final Vertx vertx, @Named("config") final JsonObject config, RedisAPI redisApi) throws InterruptedException {
        String options = config.getString("idworker");
        if (StringUtils.isBlank(options)) {
            return new IdWorker3();
        }

        options = options.trim();

        try {
            final int nodeId = Integer.valueOf(options);
            return new IdWorker3(nodeId);
        } catch (final NumberFormatException e) {
            final Future<Integer> future = redisApi.incr(REDIS_KEY_ID_WORKER_PREFIX + options)
                    .compose(res -> {
                        log.debug("idworker.incr result: {}", res);
                        if (res == null) {
                            throw new RuntimeException("通过Redis递增nodeId失败");
                        }

                        return Future.succeededFuture(Integer.valueOf(res.toString()));
                    }).recover(err -> {
                        log.error("idworker通过Redis生成nodeId失败", err);
                        return Future.failedFuture(err);
                    });
            future.wait();
            final Integer nodeId = future.result();
            if (nodeId == null) {
                throw new RuntimeException();
            }
            log.info("生成的nodeId为: {}", nodeId);
            return new IdWorker3(nodeId);
        }
    }

}
