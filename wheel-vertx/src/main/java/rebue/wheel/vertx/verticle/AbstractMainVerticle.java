package rebue.wheel.vertx.verticle;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

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
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.vertx.guice.GuiceVerticleFactory;
import rebue.wheel.vertx.guice.VertxGuiceModule;
import rebue.wheel.vertx.spi.MessageCodecAdapter;

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
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS      // 按默认的时间格式 yyyy-MM-dd'T'HH:mm:ss.SSS
                // 转换有时会报错
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
    private String                      mainId;

    private final List<String>          deploymentIds = new LinkedList<>();

    private MessageConsumer<JsonObject> configChangedConsumer;

    @Override
    public void start(Promise<Void> startPromise) {
        log.info("MainVerticle start");
        log.info("准备读取stores配置文件");
        Path storesConfigFilePath = Path.of("config", "stores.yml");
        if (!Files.exists(storesConfigFilePath)) {
            log.info("config/stores.yml 文件不存在，自动生成stores配置: {}", storesConfigFilePath);
            ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions();
            addStoreWithFile(configRetrieverOptions, Path.of("config", "config.json"));
            addStoreWithFile(configRetrieverOptions, Path.of("config", "application.json"));
            addStoreWithFile(configRetrieverOptions, Path.of("config", "config.yml"));
            addStoreWithFile(configRetrieverOptions, Path.of("config", "application.yml"));
            loadConfig(startPromise, configRetrieverOptions);
            return;
        }

        log.info("加载stores配置文件: {}", storesConfigFilePath);
        ConfigRetrieverOptions storesConfigRetrieverOptions = new ConfigRetrieverOptions();
        addStoreWithFile(storesConfigRetrieverOptions, storesConfigFilePath);
        ConfigRetriever storesConfigRetriever = ConfigRetriever.create(this.vertx, storesConfigRetrieverOptions);
        storesConfigRetriever.getConfig().compose(configRetrieverOptions -> {
            log.info("configRetrieverOptions: {}", configRetrieverOptions);
            return loadConfig(startPromise, new ConfigRetrieverOptions(configRetrieverOptions));
        }).onSuccess(v -> storesConfigRetriever.listen(this::listenConfigChange)).recover(err -> {
            log.error("加载stores配置文件失败", err);
            startPromise.fail(err);
            return Future.failedFuture(err);
        });
    }

    private static void addStoreWithFile(ConfigRetrieverOptions configRetrieverOptions, Path filePath) {
        if (Files.exists(filePath)) {
            String fileName = filePath.toString();
            String fileExt  = fileName.substring(fileName.lastIndexOf(".") + 1);
            String fileFormat;
            switch (fileExt) {
            case "json" ->
                fileFormat = "json";
            case "yml" ->
                fileFormat = "yaml";
            default ->
                throw new IllegalArgumentException("不支持的配置文件类型: " + fileName);
            }
            configRetrieverOptions.addStore(new ConfigStoreOptions()
                    .setType("file")
                    .setFormat(fileFormat)
                    .setOptional(true)
                    .setConfig(new JsonObject().put("path", filePath)));
        }
    }

    private Future<?> loadConfig(Promise<Void> startPromise, ConfigRetrieverOptions configRetrieverOptions) {
        log.info("加载配置选项");
        ConfigRetriever configRetriever = ConfigRetriever.create(this.vertx, configRetrieverOptions);
        configRetriever.setConfigurationProcessor(config -> {
            String key        = "config.json";
            String jsonConfig = config.getString(key);
            if (StringUtils.isBlank(jsonConfig)) {
                key        = "application.json";
                jsonConfig = config.getString(key);
            }
            if (StringUtils.isNotBlank(jsonConfig)) {
                config.mergeIn(new JsonObject(jsonConfig));
                config.remove(key);
            }

            key = "config.yml";
            String yamlConfig = config.getString(key);
            if (StringUtils.isBlank(yamlConfig)) {
                key        = "application.yml";
                yamlConfig = config.getString(key);
            }
            if (StringUtils.isNotBlank(yamlConfig)) {
                Yaml yaml = new Yaml();
                config.mergeIn(JsonObject.mapFrom(yaml.load(yamlConfig)));
                config.remove(key);
            }
            return config;
        });
        return configRetriever.getConfig().compose(config -> startWithConfig(startPromise, config))
                .onSuccess(v -> configRetriever.listen(this::listenConfigChange))
                .recover(err -> {
                    log.error("启动失败.", err);
                    if (startPromise != null) {
                        startPromise.fail(err);
                    }
                    return this.vertx.close().compose(v -> Future.failedFuture(err));
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
        log.trace("上一次的配置: \n{}", previousConfiguration.encode());
        log.trace("新配置: \n{}", newConfiguration.encode());
        log.info("发布配置改变的消息");
        this.vertx.eventBus().publish(EVENT_BUS_CONFIG_CHANGED + "::" + this.mainId, newConfiguration);
    }

    /**
     * 处理配置改变
     */
    @SneakyThrows
    private void handleConfigChange(Message<JsonObject> message) {
        log.info("处理配置改变");
        this.configChangedConsumer.unregister(res -> {
            log.info("undeploy verticles");
            final List<Future<Void>> undeployFutures = new LinkedList<>();
            for (String deploymentId : deploymentIds) {
                undeployFutures.add(this.vertx.undeploy(deploymentId));
            }

            Future.all(undeployFutures)
                    .onSuccess(compositeFuture -> {
                        log.info("取消部署verticle完成");
                        this.vertx.verticleFactories().forEach(verticleFactory -> {
                            log.info("unregisterVerticleFactory: {}", verticleFactory.prefix());
                            this.vertx.unregisterVerticleFactory(verticleFactory);
                        });
                        this.vertx.close();
                    }).onFailure(err -> log.error("取消部署verticle失败", err));
        });
    }

    /**
     * 带配置项运行
     *
     * @param startPromise 运行状态控制
     * @param config       配置项
     */
	@SuppressWarnings("unchecked")
	private Future<?> startWithConfig(final Promise<Void> startPromise, final JsonObject config) {
        log.info("start with config");
        log.info("添加注入模块");
        final List<Module> guiceModules = new LinkedList<>();
        log.info("添加默认的注入模块VertxGuiceModule");
        guiceModules.add(new VertxGuiceModule(this.vertx, config));
        log.info("通过SPI加载AbstractModule模块");
        ServiceLoader<Module> guiceModuleServiceLoader = ServiceLoader.load(Module.class);
        guiceModuleServiceLoader.forEach(guiceModules::add);
        log.info("添加自定义的注入模块");
        addGuiceModules(guiceModules);
        log.info("创建注入器");
        final Injector injector = Guice.createInjector(guiceModules);
        log.debug("注入自己(MainVerticle实例)的属性");
        injector.injectMembers(this);
        log.info("注册GuiceVerticleFactory工厂");
        this.vertx.registerVerticleFactory(new GuiceVerticleFactory(injector));

        log.info("注册事件总线解码器");
		@SuppressWarnings("rawtypes")
		ServiceLoader<MessageCodecAdapter> messageCodecAdapterServiceLoader = ServiceLoader.load(MessageCodecAdapter.class);		
        for (MessageCodecAdapter<Object> messageCodecAdapter : messageCodecAdapterServiceLoader) {
            log.info("注册事件总线解码器: {}", messageCodecAdapter.name());
            // noinspection unchecked
            this.vertx.eventBus().registerDefaultCodec(
                    messageCodecAdapter.messageClass(), messageCodecAdapter.messageCodec());
        }

        log.info("部署前事件");
        beforeDeploy();

        log.info("部署verticle");
        final Map<String, Class<? extends Verticle>> verticleClasses = new LinkedHashMap<>();
        addVerticleClasses(verticleClasses);
        deploymentIds.clear();
        final List<Future<String>> deployFutures = new LinkedList<>();
        for (final Entry<String, Class<? extends Verticle>> entry : verticleClasses.entrySet()) {
            final JsonObject configJsonObject = config.getJsonObject(entry.getKey());
            if (configJsonObject == null) {
                deployFutures.add(this.vertx.deployVerticle("guice:" + entry.getValue().getName())
                        .onSuccess(deploymentIds::add));
            } else {
                deployFutures.add(this.vertx
                        .deployVerticle("guice:" + entry.getValue().getName(), new DeploymentOptions(configJsonObject))
                        .onSuccess(deploymentIds::add));
            }
        }

        // 部署成功或失败事件
        return Future.all(deployFutures)
                .onSuccess(handle -> {
                    log.info("部署Verticle完成，发布部署成功的消息");
                    final String deploySuccessEventBusAddress = EVENT_BUS_DEPLOY_SUCCESS + "::" + this.mainId;
                    log.info("MainVerticle.EVENT_BUS_DEPLOY_SUCCESS address is {}", deploySuccessEventBusAddress);
                    EventBus eventBus = this.vertx.eventBus();
                    eventBus.publish(deploySuccessEventBusAddress, null);

                    log.info("监听配置改变的消息");
                    final String configChangedEventBusAddress = EVENT_BUS_CONFIG_CHANGED + "::" + this.mainId;
                    log.info("MainVerticle.EVENT_BUS_CONFIG_CHANGED address is {}", configChangedEventBusAddress);
                    this.configChangedConsumer = eventBus.consumer(configChangedEventBusAddress,
                            this::handleConfigChange);

                    log.info("是否开启 native transport: {}", vertx.isNativeTransportEnabled());
                    log.info("启动完成.");
                    if (startPromise != null) {
                        startPromise.complete();
                    }
                });
    }

    /**
     * 添加guice模块
     *
     * @param guiceModules 添加guice模块到此列表
     */
    protected void addGuiceModules(List<Module> guiceModules) {
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
