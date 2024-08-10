package rebue.wheel.vertx.web;

import org.apache.commons.lang3.StringUtils;

import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.SecurityPolicyHandler;
import io.vertx.redis.client.RedisAPI;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.api.dic.HttpStatusCodeDic;
import rebue.wheel.vertx.config.WebProperties;

@Slf4j
public class BlackListHandler implements SecurityPolicyHandler {
    private final RedisAPI redisApi;
    private final String   redisPrefix;

    @SneakyThrows
    public BlackListHandler(WebProperties.BlackListProperties blackListProperties, RedisAPI redisApi) {
        log.info("初始化黑名单处理器");
        if (redisApi == null) {
            throw new IllegalArgumentException("黑名单处理器依赖 Redis，必须保证 Redis 正确加载");
        }
        this.redisApi = redisApi;

        if (StringUtils.isBlank(blackListProperties.getRedisPrefix())) {
            throw new IllegalArgumentException("必须配置 redisPrefix 参数");
        }
        this.redisPrefix = blackListProperties.getRedisPrefix();
    }

    @Override
    public void handle(RoutingContext routingContext) {
        String srcIp = routingContext.request().remoteAddress().host();
        this.redisApi.get(this.redisPrefix + srcIp + "{" + srcIp + "}").onSuccess(result -> {
            if (result == null) {
                routingContext.next();
            } else {
                routingContext.fail(HttpStatusCodeDic.FORBIDDEN.getCode());
            }
        }).onFailure(err -> log.error("Redis获取黑名单出错", err));
    }
}
