package rebue.wheel;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 泛型工具类
 * 
 * @author zbz
 *
 */
public class JacksonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String bean2Json(final Object obj) throws IOException {
        return mapper.writeValueAsString(obj);
    }

    /**
     * 演示如何反序列化泛型类
     */
    @SuppressWarnings("unchecked")
    public static <T> T json2BeanByType(final String jsonStr, @SuppressWarnings("rawtypes") final TypeReference tr)
            throws IOException {
        return (T) mapper.readValue(jsonStr, tr);
    }

    public static <T> T json2Bean(final String jsonStr, final Class<T> clazz) throws IOException {
        return mapper.readValue(jsonStr, clazz);
    }
}
