package rebue.wheel.kafka.httpsink;

import static rebue.wheel.kafka.httpsink.HttpSinkCst.*;

import java.util.Map;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

public class HttpSinkConfig extends AbstractConfig {

    public HttpSinkConfig(final Map<String, String> properties) {
        super(configDef(), properties);
    }

    public static ConfigDef configDef() {
        final ConfigDef configDef = new ConfigDef();
        configDef.define(
                CONFIG_KEY_HTTP_API_URL,
                ConfigDef.Type.STRING,
                ConfigDef.NO_DEFAULT_VALUE,
                new UrlValidator(false),
                ConfigDef.Importance.HIGH,
                "The URL to send data to.",
                CONFIG_DEFAULT_GROUP,
                1,
                ConfigDef.Width.LONG,
                CONFIG_KEY_HTTP_API_URL);
        configDef.define(
                CONFIG_KEY_HTTP_VERSION,
                ConfigDef.Type.STRING,
                "HTTP_1_1",
                new HttpVersionValidator(false),
                ConfigDef.Importance.LOW,
                "Version of Http Protocol.",
                CONFIG_DEFAULT_GROUP,
                1,
                ConfigDef.Width.SHORT,
                CONFIG_KEY_HTTP_VERSION);
        return configDef;
    }

}
