package rebue.wheel.vertx.web;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.SecurityPolicyHandler;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.RedisAPI;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.api.dic.HttpStatusCodeDic;
import rebue.wheel.vertx.config.WebProperties;

/**
 * 限流处理器
 */
@Slf4j
public class LimitRateHandler implements SecurityPolicyHandler {
    private final RedisAPI redisApi;
    private final String   globalRedisKey;
    private final String   srcIpRedisKeyPrefix;
    private String         scriptSha;

    @SneakyThrows
    public LimitRateHandler(WebProperties.LimitRateProperties limitRateProps, RedisAPI redisApi) {
        log.info("初始化限流处理器");
        if (redisApi == null) {
            throw new IllegalArgumentException("限流处理器依赖 Redis，必须保证 Redis 正确加载");
        }
        this.redisApi = redisApi;

        if (StringUtils.isBlank(limitRateProps.getGlobalRedisKeyPrefix())) {
            throw new IllegalArgumentException("必须配置 globalRedisKey 参数");
        }
        this.globalRedisKey = limitRateProps.getGlobalRedisKeyPrefix();
        if (StringUtils.isBlank(limitRateProps.getSrcIpRedisKeyPrefix())) {
            throw new IllegalArgumentException("必须配置 srcIpRedisKeyPrefix 参数");
        }
        this.srcIpRedisKeyPrefix = limitRateProps.getSrcIpRedisKeyPrefix();

        String script;
        try (InputStream inputStream = LimitRateHandler.class.getResourceAsStream("/script/TimeSlidingWindowRateLimiter.lua")) {
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
        log.debug("进入限流处理器");
        HttpServerRequest request = routingContext.request();
        if (!request.isEnded()) {
            log.debug("暂停请求");
            request.pause();
        }

        // 先判断全局是否限流
        limitRate(globalRedisKey + "config", globalRedisKey + "window").onSuccess(globalRes -> {
            // 如果全局不限流，则判断此来源的IP是否限流
            if (globalRes) {
                String srcIp  = request.remoteAddress().hostAddress();
                String prefix = srcIpRedisKeyPrefix + srcIp;
                limitRate(prefix + ".config{" + srcIp + "}", prefix + ".window{" + srcIp + "}").onSuccess(ipRes -> {
                    resumeRequest(request);
                    if (ipRes) {
                        routingContext.next();
                    } else {
                        routingContext.fail(HttpStatusCodeDic.TOO_MANY_REQUESTS.getCode());
                    }
                }).onFailure(err -> {
                    log.error("Redis 执行IP限流的 Lua 脚本出错", err);
                    resumeRequest(request);
                    routingContext.fail(HttpStatusCodeDic.BAD_GATEWAY.getCode());
                });
            } else {
                resumeRequest(request);
                routingContext.fail(HttpStatusCodeDic.TOO_MANY_REQUESTS.getCode());
            }
        }).onFailure(err -> {
            log.error("Redis 执行全局限流的 Lua 脚本出错", err);
            resumeRequest(request);
            routingContext.fail(HttpStatusCodeDic.BAD_GATEWAY.getCode());
        });
    }

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
     *             [0]: 配置的 key
     *             [1]: 窗口的 key
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
