package rebue.wheel.vertx.verticle;

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
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.guice.GuiceVerticleFactory;
import rebue.wheel.vertx.guice.VertxGuiceModule;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.Map.Entry;

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
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)  // 浮点型用BigDecimal处理
                // .enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS) // 整型用BigInteger处理
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
        log.info("MainVerticle start");

        final ConfigRetriever retriever = ConfigRetriever.create(this.vertx);
        retriever.getConfig(configRes -> {
            if (configRes.failed()) {
                log.warn("Get config failed", configRes.cause());
                startPromise.fail(configRes.cause());
                return;
            }

            final JsonObject config = configRes.result();
            if (config == null || config.isEmpty()) {
                startPromise.fail("Get config is empty");
                return;
            }

            final JsonArray stores = config.getJsonArray("stores");
            if (stores == null) {
                startWithConfig(startPromise, config);
                return;
            }

            log.info("配置中心列表: {}", stores);
            final ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions();
            stores.forEach(store -> {
                final ConfigStoreOptions storeOptions = new ConfigStoreOptions((JsonObject) store);
                configRetrieverOptions.addStore(storeOptions);
            });

            final ConfigRetriever configServerRetriever = ConfigRetriever.create(this.vertx, configRetrieverOptions);
            configServerRetriever.getConfig(configServerConfigRes -> {
                if (configServerConfigRes.failed()) {
                    log.warn("Get server config failed", configServerConfigRes.cause());
                    startPromise.fail(configServerConfigRes.cause());
                    return;
                }

                final JsonObject configServerConfig = configServerConfigRes.result();
                if (configServerConfig == null || configServerConfig.isEmpty()) {
                    startPromise.fail("Get server config is empty");
                    return;
                }

                startWithConfig(startPromise, configServerConfig);
            });
        });
    }

    /**
     * 带配置项运行
     *
     * @param startPromise 运行状态控制
     * @param config       配置项
     */
    private void startWithConfig(final Promise<Void> startPromise, final JsonObject config) {
        log.info("添加注入模块");
        final List<Module> guiceModules = new LinkedList<>();
        // 添加默认的注入模块
        guiceModules.add(new VertxGuiceModule(this.vertx, config));
        // 添加自定义的注入模块
        addGuiceModules(guiceModules);
        log.info("创建注入器");
        final Injector injector = Guice.createInjector(guiceModules);
        log.debug("注入自己(MainVerticle实例)的属性");
        injector.injectMembers(this);
        log.info("注册GuiceVerticleFactory工厂");
        this.vertx.registerVerticleFactory(new GuiceVerticleFactory(injector));

        log.info("部署前事件");
        beforeDeploy();

        log.info("部署verticle");
        final Map<String, Class<? extends Verticle>> verticleClasses = new LinkedHashMap<>();
        addVerticleClasses(verticleClasses);
        @SuppressWarnings("rawtypes") final List<Future> deployFutures = new LinkedList<>();
        for (final Entry<String, Class<? extends Verticle>> entry : verticleClasses.entrySet()) {
            final JsonObject configJsonObject = config.getJsonObject(entry.getKey());
            if (configJsonObject == null) {
                deployFutures.add(this.vertx.deployVerticle("guice:" + entry.getValue().getName()));
            } else {
                deployFutures.add(this.vertx.deployVerticle("guice:" + entry.getValue().getName(), new DeploymentOptions(configJsonObject)));
            }
        }

        // 部署成功或失败事件
        CompositeFuture.all(deployFutures)
                .onSuccess(handle -> {
                    log.info("部署Verticle完成，发布部署成功的消息");
                    final String address = EVENT_BUS_DEPLOY_SUCCESS + "::" + this.mainId;
                    log.info("MainVerticle.EVENT_BUS_DEPLOY_SUCCESS address is " + address);
                    this.vertx.eventBus().publish(address, null);
                    log.info("是否开启 native transport: {}", vertx.isNativeTransportEnabled());
                    log.info("启动完成.");
                    startPromise.complete();
                })
                .onFailure(err -> {
                    log.error("启动失败.", err);
                    startPromise.fail(err);
                    this.vertx.close();
                });

    }

    /**
     * 添加guice模块
     *
     * @param guiceModules 添加guice模块到此列表
     */
    protected void addGuiceModules(final List<Module> guiceModules) {
    }

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
    public void stop() {
        log.info("MainVerticle stop");
    }

}
