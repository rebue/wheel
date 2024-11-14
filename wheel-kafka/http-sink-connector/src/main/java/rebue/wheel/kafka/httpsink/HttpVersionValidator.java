package rebue.wheel.kafka.httpsink;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;


public class HttpVersionValidator implements ConfigDef.Validator {
    private final boolean isNullable;

    public HttpVersionValidator(boolean isNullable) {
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

        switch ((String) value) {
            case "HTTP_1_1":
            case "HTTP_2":
                break;
            default:
                throw new ConfigException(name, value, "must be HTTP_1_1 or HTTP_2");
        }
    }
}
