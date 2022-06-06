package rebue.wheel.vertx.verticle;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.guice.GuiceVerticleFactory;

@SuppressWarnings("deprecation")
@Slf4j
public abstract class AbstractMainVerticle extends AbstractVerticle {
    public static final String                       EVENT_BUS_DEPLOY_SUCCESS = "rebue.wheel.vertx.verticle.main-verticle.deploy-success";

    protected Map<String, Class<? extends Verticle>> verticleClasses          = new LinkedHashMap<>();

    static {
        // 初始化jackson的功能
        DatabindCodec.mapper()
                .disable(
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES   // 忽略没有的字段
                )
                .enable(
                        MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES    // 忽略字段和属性的大小写
                )
                .setSerializationInclusion(Include.NON_NULL)                // 不序列化值为null的字段
                .registerModule(new JavaTimeModule());                      // 支持Java8的LocalDate/LocalDateTime类型
    }

    protected abstract void initVerticleClasses();

    @Override
    public void init(final Vertx vertx, final Context context) {
        super.init(vertx, context);
        initVerticleClasses();
    }

    @Override
    public void start(final Promise<Void> startPromise) {
        final ConfigRetriever retriever = ConfigRetriever.create(this.vertx);
        retriever.getConfig(configRes -> {
            log.info("config result: {}", configRes.result());

            if (configRes.failed()) {
                log.warn("Get config failed", configRes.cause());
                startPromise.fail(configRes.cause());
            }

            final JsonObject config = configRes.result();
            if (config == null || config.isEmpty()) {
                startPromise.fail("Get config is empty");
                return;
            }

            log.info("注册GuiceVerticleFactory工厂");
            this.vertx.registerVerticleFactory(new GuiceVerticleFactory(config));

            log.info("部署verticle");
            @SuppressWarnings("rawtypes")
            final List<Future> deployFutures = new LinkedList<>();
            for (final Entry<String, Class<? extends Verticle>> entry : this.verticleClasses.entrySet()) {
                deployFutures.add(this.vertx.deployVerticle("guice:" + entry.getValue().getName(), new DeploymentOptions(config.getJsonObject(entry.getKey()))));
            }

            // 部署成功或失败事件
            CompositeFuture.all(deployFutures)
                    .onSuccess(handle -> {
                        log.info("部署Verticle完成，发布部署成功的消息");
                        this.vertx.eventBus().publish(EVENT_BUS_DEPLOY_SUCCESS, null);
                        log.info("启动完成.");
                        startPromise.complete();
                    })
                    .onFailure(err -> {
                        log.error("启动失败.", err);
                        startPromise.fail(err);
                        this.vertx.close();
                    });
        });
    }

}
