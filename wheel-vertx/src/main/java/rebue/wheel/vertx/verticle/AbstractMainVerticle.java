package rebue.wheel.vertx.verticle;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Named;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.guice.GuiceVerticleFactory;
import rebue.wheel.vertx.guice.VertxGuiceModule;

@SuppressWarnings("deprecation")
@Slf4j
public abstract class AbstractMainVerticle extends AbstractVerticle {

    /**
     * 部署成功事件
     */
    public static final String EVENT_BUS_DEPLOY_SUCCESS = "rebue.wheel.vertx.verticle.main-verticle.deploy-success";

    static {
        // 初始化jackson的功能
        DatabindCodec.mapper()
                .disable(
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES   // 忽略没有的字段
                )
                .disable(
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS      // 按默认的时间格式'yyyy-MM-dd'T'HH:mm:ss.SSS’转换有时会报错
                )
                .enable(
                        MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES    // 忽略字段和属性的大小写
                )
                .setSerializationInclusion(Include.NON_NULL)                // 不序列化值为null的字段
                .setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"))
                .registerModules(new JavaTimeModule(),                      // 支持Java8的LocalDate/LocalDateTime类型
                        new Jdk8Module(),
                        new ParameterNamesModule());
    }

    @Inject
    @Named("mainId")
    private String mainId;

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

            log.info("创建注入器");
            final List<Module> guiceModules = new LinkedList<>();
            guiceModules.add(new VertxGuiceModule(this.vertx, config));
            addGuiceModules(guiceModules);
            final Injector injector = Guice.createInjector(guiceModules);
            // 注入自己
            injector.injectMembers(this);

            log.info("注册GuiceVerticleFactory工厂");
            this.vertx.registerVerticleFactory(new GuiceVerticleFactory(injector));

            log.info("部署前事件");
            beforeDeploy();

            log.info("部署verticle");
            final Map<String, Class<? extends Verticle>> verticleClasses = new LinkedHashMap<>();
            addVerticleClasses(verticleClasses);
            @SuppressWarnings("rawtypes")
            final List<Future> deployFutures = new LinkedList<>();
            for (final Entry<String, Class<? extends Verticle>> entry : verticleClasses.entrySet()) {
                deployFutures.add(this.vertx.deployVerticle("guice:" + entry.getValue().getName(), new DeploymentOptions(config.getJsonObject(entry.getKey()))));
            }

            // 部署成功或失败事件
            CompositeFuture.all(deployFutures)
                    .onSuccess(handle -> {
                        log.info("部署Verticle完成，发布部署成功的消息");
                        final String address = EVENT_BUS_DEPLOY_SUCCESS + "::" + this.mainId;
                        log.info("MainVerticle.EVENT_BUS_DEPLOY_SUCCESS address is " + address);
                        this.vertx.eventBus().publish(address, null);
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

    /**
     * 添加guice模块
     *
     * @param guiceModules 添加guice模块到此列表
     */
    protected abstract void addGuiceModules(List<Module> guiceModules);

    /**
     * 部署前
     */
    protected void beforeDeploy() {
    }

    /**
     * 添加要部署的Verticle类列表
     *
     * @param verticleClasses 添加Verticle类到此列表
     */
    protected abstract void addVerticleClasses(Map<String, Class<? extends Verticle>> verticleClasses);

    @Override
    public void stop() throws Exception {
        log.info("MainVerticle stop");
    }

}
