package rebue.wheel;

import java.io.IOException;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

/**
 * 泛型工具类
 *
 * @author zbz
 *
 */
public class JacksonUtils {

    private static final ObjectMapper _objectMapper = JsonMapper.builder()  // or different mapper for other format
            .enable(
                    // 反序列化时忽略大小写
                    MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .disable(
                    // 按默认的时间格式'yyyy-MM-dd'T'HH:mm:ss.SSS’转换有时会报错
                    SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            // 不转换值为null的项
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .defaultTimeZone(TimeZone.getTimeZone("Asia/Shanghai"))
            // 全局支持Java8的时间格式化
            .addModule(new ParameterNamesModule())
            .addModule(new Jdk8Module())
            .addModule(new JavaTimeModule())
            // and possibly other configuration, modules, then:
            .build();

    public static ObjectMapper getObjectMapper() {
        return _objectMapper;
    }

    public static String bean2Json(final Object obj) throws IOException {
        return _objectMapper.writeValueAsString(obj);
    }

    /**
     * 演示如何反序列化泛型类
     */
    @SuppressWarnings("unchecked")
    public static <T> T json2BeanByType(final String jsonStr, @SuppressWarnings("rawtypes") final TypeReference tr)
            throws IOException {
        return (T) _objectMapper.readValue(jsonStr, tr);
    }

    public static <T> T json2Bean(final String jsonStr, final Class<T> clazz) throws IOException {
        return _objectMapper.readValue(jsonStr, clazz);
    }
}
