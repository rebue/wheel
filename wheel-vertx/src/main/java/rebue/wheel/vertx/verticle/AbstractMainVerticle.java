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
import io.vertx.config.ConfigChange;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import lombok.SneakyThrows;
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
    /**
     * 配置改变事件
     */
    public static final String EVENT_BUS_CONFIG_CHANGED = "rebue.wheel.vertx.verticle.main-verticle.config-changed";

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

    private final List<String> deploymentIds = new LinkedList<>();

    private MessageConsumer<JsonObject> configChangedConsumer;


    @Override
    public void start(final Promise<Void> startPromise) {
        log.info("MainVerticle start");

        final ConfigRetriever defaultConfigRetriever = ConfigRetriever.create(this.vertx);
        defaultConfigRetriever.getConfig(defaultConfigRes -> {
            if (defaultConfigRes.failed()) {
                log.warn("Get config failed", defaultConfigRes.cause());
                startPromise.fail(defaultConfigRes.cause());
                return;
            }

            final JsonObject defaultConfigJsonObject = defaultConfigRes.result();
            if (defaultConfigJsonObject == null || defaultConfigJsonObject.isEmpty()) {
                startPromise.fail("Get config is empty");
                return;
            }

            JsonArray stores = defaultConfigJsonObject.getJsonArray("stores");
            if (stores == null) {
                startWithConfig(startPromise, defaultConfigJsonObject);
            } else {
                log.info("配置中心列表: {}", stores);
                final ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions();
                stores.forEach(store -> configRetrieverOptions.addStore(new ConfigStoreOptions((JsonObject) store)));

                ConfigRetriever storeConfigRetriever = ConfigRetriever.create(this.vertx, configRetrieverOptions);
                storeConfigRetriever.getConfig(storeConfigRes -> {
                    if (storeConfigRes.failed()) {
                        log.warn("Get store config failed", storeConfigRes.cause());
                        startPromise.fail(storeConfigRes.cause());
                        return;
                    }

                    final JsonObject storeConfigJsonObject = storeConfigRes.result();
                    if (storeConfigJsonObject == null || storeConfigJsonObject.isEmpty()) {
                        startPromise.fail("Get store config is empty");
                        return;
                    }

                    startWithConfig(startPromise, storeConfigJsonObject);
                });

                storeConfigRetriever.listen(this::listenConfigChange);
            }

            defaultConfigRetriever.listen(this::listenConfigChange);
        });
    }

    /**
     * 监听配置改变事件
     *
     * @param configChange 配置改变对象
     */
    private void listenConfigChange(ConfigChange configChange) {
        log.info("配置有变动");
        JsonObject previousConfiguration = configChange.getPreviousConfiguration();
        JsonObject newConfiguration      = configChange.getNewConfiguration();
        log.info("上一次的配置: \n{}", previousConfiguration.encode());
        log.info("新配置: \n{}", newConfiguration.encode());
        log.info("发布配置改变的消息");
        this.vertx.eventBus().publish(EVENT_BUS_CONFIG_CHANGED + "::" + this.mainId, newConfiguration);
    }

    /**
     * 处理配置改变
     */
    @SneakyThrows
    private void handleConfigChange(Message<JsonObject> message) {
        JsonObject newConfiguration = message.body();
        log.info("处理配置改变");
        this.configChangedConsumer.unregister();
        log.info("undeploy verticles");
        deploymentIds.forEach(deploymentId -> this.vertx.undeploy(deploymentId));
//        this.start();
        startWithConfig(null, newConfiguration);
    }

    /**
     * 带配置项运行
     *
     * @param startPromise 运行状态控制
     * @param config       配置项
     */
    private void startWithConfig(final Promise<Void> startPromise, final JsonObject config) {
        log.info("start with config");
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
        deploymentIds.clear();
        @SuppressWarnings("rawtypes") final List<Future> deployFutures = new LinkedList<>();
        for (final Entry<String, Class<? extends Verticle>> entry : verticleClasses.entrySet()) {
            final JsonObject configJsonObject = config.getJsonObject(entry.getKey());
            if (configJsonObject == null) {
                deployFutures.add(this.vertx.deployVerticle("guice:" + entry.getValue().getName())
                        .onSuccess(deploymentIds::add));
            } else {
                deployFutures.add(this.vertx.deployVerticle("guice:" + entry.getValue().getName(), new DeploymentOptions(configJsonObject))
                        .onSuccess(deploymentIds::add));
            }
        }

        // 部署成功或失败事件
        CompositeFuture.all(deployFutures)
                .onSuccess(handle -> {
                    log.info("部署Verticle完成，发布部署成功的消息");
                    final String deploySuccessEventBusAddress = EVENT_BUS_DEPLOY_SUCCESS + "::" + this.mainId;
                    log.info("MainVerticle.EVENT_BUS_DEPLOY_SUCCESS address is " + deploySuccessEventBusAddress);
                    this.vertx.eventBus().publish(deploySuccessEventBusAddress, null);

                    log.info("监听配置改变的消息");
                    final String configChangedEventBusAddress = EVENT_BUS_CONFIG_CHANGED + "::" + this.mainId;
                    log.info("MainVerticle.EVENT_BUS_CONFIG_CHANGED address is " + configChangedEventBusAddress);
                    this.configChangedConsumer = this.vertx.eventBus().consumer(configChangedEventBusAddress, this::handleConfigChange);

                    log.info("是否开启 native transport: {}", vertx.isNativeTransportEnabled());
                    log.info("启动完成.");
                    if (startPromise != null) startPromise.complete();
                })
                .onFailure(err -> {
                    log.error("启动失败.", err);
                    if (startPromise != null) startPromise.fail(err);
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
