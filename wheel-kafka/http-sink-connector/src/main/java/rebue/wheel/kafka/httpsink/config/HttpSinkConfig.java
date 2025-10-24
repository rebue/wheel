package rebue.wheel.kafka.httpsink.config;

import java.util.Map;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

public class HttpSinkConfig extends AbstractConfig {
    /**
     * 配置的默认组
     */
    public static final String CONFIG_DEFAULT_GROUP         = "default";
    /**
     * HTTP 协议配置的Key
     */
    public static final String CONFIG_KEY_HTTP_VERSION      = "http.version";
    /**
     * API请求的地址配置的Key
     */
    @Deprecated
    public static final String CONFIG_KEY_HTTP_API_URL      = "http.api.url";
    /**
     * Http请求的地址配置的Key
     */
    public static final String CONFIG_KEY_HTTP_URL          = "http.url";
    /**
     * Http请求的header的Content-Type配置的Key
     */
    public static final String CONFIG_KEY_HTTP_CONTENT_TYPE = "http.header.content-type";

    public HttpSinkConfig(final Map<String, String> properties) {
        super(configDef(), properties);
    }

    public static ConfigDef configDef() {
        final ConfigDef configDef = new ConfigDef();
        configDef.define(
                CONFIG_KEY_HTTP_VERSION,
                ConfigDef.Type.STRING,
                "HTTP_1_1",
                new HttpVersionValidator(false),
                ConfigDef.Importance.LOW,
                "HTTP protocol version configuration",
                CONFIG_DEFAULT_GROUP,
                1,
                ConfigDef.Width.SHORT,
                CONFIG_KEY_HTTP_VERSION);
        configDef.define(
                CONFIG_KEY_HTTP_API_URL,
                ConfigDef.Type.STRING,
                null,
                new UrlValidator(true),
                ConfigDef.Importance.LOW,
                "Deprecated API request URL configuration",
                CONFIG_DEFAULT_GROUP,
                1,
                ConfigDef.Width.LONG,
                CONFIG_KEY_HTTP_API_URL);
        configDef.define(
                CONFIG_KEY_HTTP_URL,
                ConfigDef.Type.STRING,
                null,
                new UrlValidator(true),
                ConfigDef.Importance.HIGH,
                "HTTP request URL configuration",
                CONFIG_DEFAULT_GROUP,
                1,
                ConfigDef.Width.LONG,
                CONFIG_KEY_HTTP_URL);
        configDef.define(
                CONFIG_KEY_HTTP_CONTENT_TYPE,
                ConfigDef.Type.STRING,
                "application/json",
                null,
                ConfigDef.Importance.LOW,
                "HTTP request header Content-Type configuration",
                CONFIG_DEFAULT_GROUP,
                1,
                ConfigDef.Width.SHORT,
                CONFIG_KEY_HTTP_CONTENT_TYPE);
        return configDef;
    }

}
