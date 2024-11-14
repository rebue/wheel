package rebue.wheel.kafka.httpsink;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

public class UrlValidator implements ConfigDef.Validator {
    private final boolean isNullable;

    public UrlValidator(boolean isNullable) {
        this.isNullable = isNullable;
    }

    @Override
    public void ensureValid(String name, Object value) {
        if (isNullable && value == null) {
            return;
        }

        if (value == null) {
            throw new ConfigException(name, null, "can't be null");
        }

        if (!(value instanceof String)) {
            throw new ConfigException(name, value, "must be string");
        }

        if (((String) value).isEmpty()) {
            throw new ConfigException(name, value, "can't be empty");
        }

        try {
            new URL((String) value);
        } catch (final MalformedURLException e) {
            throw new ConfigException(name, value, "malformed URL");
        }
    }
}
