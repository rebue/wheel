package rebue.wheel.vertx.web;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Injector;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

/**
 * 全局限流处理器
 */
@Slf4j
public class GlobalLimitRateHandler extends AbstractLimitRateHandler {
    /**
     * 全局 redis key 的前缀，后面为 config 或 window
     * config 的值使用 Hash 存储 size、limit、expires
     * window 的值使用 Sorted Set 存储，用 ZADD/ZCARD/ZREMRANGEBYSCORE 来限流
     */
    private String redisKeyPrefix;

    @Override
    public String name() {
        return "global";
    }

    @Override
    protected String luaPath() {
        return "/redis-lua/TimeSlidingWindowRateLimiter.lua";
    }

    /**
     * @param vertx    vertx实例
     * @param router   路由器
     * @param injector 注入器
     * @param options  处理器的配置选项
     *
     *
     *
     */
    @Override
    public void init(Vertx vertx, Router router, Injector injector, Map<String, Object> options) {
        super.init(vertx, router, injector, options);
        String redisKeyPrefix = Optional.ofNullable(options.get("redisKeyPrefix")).map(Object::toString).orElse("");
        if (StringUtils.isBlank(redisKeyPrefix)) {
            throw new IllegalArgumentException("必须配置 web.limitRate." + this.name() + ".redisKeyPrefix 参数");
        }
        this.redisKeyPrefix = redisKeyPrefix + "global";
    }

    @Override
    protected String[] buildLuaArgs(HttpServerRequest request) {
        return new String[] { redisKeyPrefix + ".config", redisKeyPrefix + ".window" };
    }
}
