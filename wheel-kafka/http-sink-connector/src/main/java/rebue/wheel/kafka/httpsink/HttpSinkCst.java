package rebue.wheel.kafka.httpsink;

public interface HttpSinkCst {
    /**
     * Kafka 插件的名称
     */
    String PLUGIN_NAME             = "http-sink-connector";
    /**
     * Kafka 插件的版本
     */
    String PLUGIN_VERSION          = "3.8.60";
    /**
     * 配置的默认组
     */
    String CONFIG_DEFAULT_GROUP    = "default";
    /**
     * API 请求的地址配置的Key
     */
    String CONFIG_KEY_HTTP_API_URL = "http.api.url";
    /**
     * HTTP 协议配置的Key
     */
    String CONFIG_KEY_HTTP_VERSION = "http.version";

}
