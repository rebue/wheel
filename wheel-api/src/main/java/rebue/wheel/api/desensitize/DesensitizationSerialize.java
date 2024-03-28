package rebue.wheel.api.desensitize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * 脱敏序列化器
 */
@NoArgsConstructor
@AllArgsConstructor
public class DesensitizationSerialize extends JsonSerializer<String> implements ContextualSerializer {
    /**
     * 脱敏策略
     */
    private DesensitizeStrategy desensitizeStrategy;

    /**
     * @return 匹配的正则表达式
     */
    private String regex;

    /**
     * @return 要替换的表达式
     */
    private String replacement;

    @Override
    public void serialize(String value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (DesensitizeStrategy.CUSTOM == desensitizeStrategy) {
            if (StringUtils.isBlank(value)) value = "";
            value = value.trim();
            generator.writeString(value.replaceAll(regex, replacement));
        } else {
            generator.writeString(desensitizeStrategy.getDesensitizer().apply(value));
        }
    }

    /**
     * 从上下文信息中获取注解的参数通过构造传递给序列化器
     *
     * @param provider     Serializer provider to use for accessing config, other serializers
     * @param beanProperty Method or field that represents the property
     *                     (and is used to access value to serialize).
     *                     Should be available; but there may be cases where caller cannot provide it and
     *                     null is passed instead (in which case impls usually pass 'this' serializer as is)
     * @return JsonSerializer
     * @throws JsonMappingException
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty == null) {
            return provider.findNullValueSerializer(null);
        }

        // 非 String 类直接跳过
        if (!Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
            return provider.findValueSerializer(beanProperty.getType(), beanProperty);
        }

        Desensitize desensitize = beanProperty.getAnnotation(Desensitize.class);
        if (desensitize == null) {
            desensitize = beanProperty.getContextAnnotation(Desensitize.class);
        }

        if (desensitize == null) {
            return provider.findValueSerializer(beanProperty.getType(), beanProperty);
        }

        return new DesensitizationSerialize(desensitize.value(), desensitize.regex(), desensitize.replacement());
    }
}
