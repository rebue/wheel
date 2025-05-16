package rebue.wheel.vertx.web;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.inject.Injector;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.RedisAPI;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.api.dic.HttpStatusCodeDic;
import rebue.wheel.vertx.spi.LimitRateHandler;

/**
 * 限流处理器
 */
@Slf4j
public abstract class AbstractLimitRateHandler implements LimitRateHandler {
    private RedisAPI redisApi;
    private String   scriptSha;

    @Override
    @SneakyThrows
    public void init(Vertx vertx, Router router, Injector injector, Map<String, Object> options) {
        log.info("初始化{}限流处理器", this.name());
        redisApi = injector.getInstance(RedisAPI.class);
        if (redisApi == null) {
            throw new IllegalArgumentException("限流处理器依赖 Redis，必须保证 Redis 正确加载");
        }

        String script;
        try (InputStream inputStream = AbstractLimitRateHandler.class.getResourceAsStream(this.luaPath())) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String        line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
                script = sb.toString();
            }
        }

        this.redisApi.send(Command.SCRIPT, "load", script).onSuccess(res -> {
            scriptSha = res.toString();
            log.info("Redis 加载限流的 Lua 脚本: {}", scriptSha);
        }).onFailure(err -> log.error("Redis 加载限流的 Lua 脚本出错", err));
    }

    @Override
    public void handle(RoutingContext routingContext) {
        log.debug("请求进入{}限流处理器", this.name());
        HttpServerRequest request = routingContext.request();
        if (!request.isEnded()) {
            log.debug("暂停请求");
            request.pause();
        }

        // 调用 lua 限流判断是否要拒绝请求
        limitRate(buildLuaArgs(request)).onSuccess(res -> {
            try {
                // 如果全局限流未拒绝请求，则下一步
                if (res) {
                    routingContext.next();
                }
                // 否则返回请求过多
                else {
                    routingContext.fail(HttpStatusCodeDic.TOO_MANY_REQUESTS.getCode());
                }
            } finally {
                // 恢复请求
                resumeRequest(request);
            }
        }).onFailure(err -> {
            log.error("Redis 执行全局限流的 Lua 脚本出错", err);
            resumeRequest(request);
            routingContext.fail(HttpStatusCodeDic.BAD_GATEWAY.getCode());
        });
    }

    /**
     * lua 脚本路径
     *
     * @return lua 脚本路径
     */
    protected abstract String luaPath();

    protected abstract String[] buildLuaArgs(HttpServerRequest request);

    /**
     * 恢复请求
     * 
     * @param request 请求对象
     */
    private static void resumeRequest(HttpServerRequest request) {
        if (!request.isEnded()) {
            log.debug("恢复请求");
            request.resume();
        }
    }

    /**
     * 限流
     * 
     * @param args 要传递给 lua 的参数
     * @return 是否限流
     */
    private Future<Boolean> limitRate(String... args) {
        List<String> list = new LinkedList<>();
        list.add(scriptSha);
        list.add(String.valueOf(args.length));
        Collections.addAll(list, args);
        return this.redisApi.evalsha(list).compose(res -> res.toInteger().equals(1)
                ? Future.succeededFuture(true)
                : Future.succeededFuture(false));
    }
}
