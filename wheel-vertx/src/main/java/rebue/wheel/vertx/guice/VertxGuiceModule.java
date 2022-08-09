package rebue.wheel.vertx.guice;

import java.util.TimeZone;

import javax.inject.Named;
import javax.inject.Singleton;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;

public class VertxGuiceModule extends AbstractModule {

    protected Vertx    vertx;
    private JsonObject config;

    static {
        ((DatabindCodec) io.vertx.core.json.Json.CODEC).mapper()
                .enable(
                        // 反序列化时忽略大小写
                        MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .disable(
                        // 按默认的时间格式'yyyy-MM-dd'T'HH:mm:ss.SSS’转换有时会报错
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                // 不转换值为null的项
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"))
                // 全局支持Java8的时间格式化
                .registerModules(new ParameterNamesModule(),
                        new Jdk8Module(),
                        new JavaTimeModule());
    }

    public VertxGuiceModule(final Vertx vertx, final JsonObject config) {
        this.vertx  = vertx;
        this.config = config;
    }

    @Provides
    Vertx getVertx() {
        return this.vertx;
    }

    @Provides
    EventBus getEventBus() {
        return this.vertx.eventBus();
    }

    @Provides
    @Named("config")
    JsonObject getConfig() {
        return this.config;
    }

    @Provides
    @Singleton
    @Named("mainId")
    String getMainId() {
        return NanoIdUtils.randomNanoId();
    }

}
