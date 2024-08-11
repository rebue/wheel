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
    private final String   redisPrefix;
    private String         blackListRedisPrefix;
    private String         scriptSha;

    @SneakyThrows
    public LimitRateHandler(WebProperties.LimitRateProperties limitRateProps,
            WebProperties.BlackListProperties blackListProperties, RedisAPI redisApi) {
        log.info("初始化限流处理器");
        if (redisApi == null) {
            throw new IllegalArgumentException("限流处理器依赖 Redis，必须保证 Redis 正确加载");
        }
        this.redisApi = redisApi;

        if (StringUtils.isBlank(limitRateProps.getRedisPrefix())) {
            throw new IllegalArgumentException("必须配置 redisPrefix 参数");
        }
        this.redisPrefix = limitRateProps.getRedisPrefix();

        if (blackListProperties.getEnabled()) {
            blackListRedisPrefix = blackListProperties.getRedisPrefix();
        }

        String script;
        try (InputStream inputStream = LimitRateHandler.class.getResourceAsStream(
                "/script/TimeSlidingWindowRateLimiter.lua")) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream, StandardCharsets.UTF_8))) {
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

        limitRate(redisPrefix + "size{" + redisPrefix + "global}",
                redisPrefix + "limit{" + redisPrefix + "global}",
                redisPrefix + "window{" + redisPrefix + "global}",
                redisPrefix + "expires{" + redisPrefix + "global}").onSuccess(globalRes -> {
                    if (globalRes) {
                        String srcIp = request.remoteAddress().host();
                        String[] args;
                        if (blackListRedisPrefix == null) {
                            args = new String[] {
                                    redisPrefix + srcIp + "-size{" + srcIp + "}",
                                    redisPrefix + srcIp + "-limit{" + srcIp + "}",
                                    redisPrefix + srcIp + "-window{" + srcIp + "}",
                                    redisPrefix + srcIp + "-expires{" + srcIp + "}" };
                        } else {
                            args = new String[] {
                                    redisPrefix + srcIp + "-size{" + srcIp + "}",
                                    redisPrefix + srcIp + "-limit{" + srcIp + "}",
                                    redisPrefix + srcIp + "-window{" + srcIp + "}",
                                    redisPrefix + srcIp + "-expires{" + srcIp + "}",
                                    blackListRedisPrefix + srcIp + "{" + srcIp + "}" };
                        }

                        limitRate(args).onSuccess(ipRes -> {
                            if (!request.isEnded()) {
                                log.debug("恢复请求");
                                request.resume();
                            }
                            if (ipRes) {
                                routingContext.next();
                            } else {
                                routingContext.fail(HttpStatusCodeDic.TOO_MANY_REQUESTS.getCode());
                            }
                        }).onFailure(err -> {
                            log.error("Redis 执行IP限流的 Lua 脚本出错", err);
                            if (!request.isEnded()) {
                                log.debug("恢复请求");
                                request.resume();
                            }
                            routingContext.fail(HttpStatusCodeDic.BAD_GATEWAY.getCode());
                        });
                    } else {
                        if (!request.isEnded()) {
                            log.debug("恢复请求");
                            request.resume();
                        }
                        routingContext.fail(HttpStatusCodeDic.TOO_MANY_REQUESTS.getCode());
                    }
                }).onFailure(err -> {
                    log.error("Redis 执行全局限流的 Lua 脚本出错", err);
                    if (!request.isEnded()) {
                        log.debug("恢复请求");
                        request.resume();
                    }
                    routingContext.fail(HttpStatusCodeDic.BAD_GATEWAY.getCode());
                });
    }

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
