package rebue.wheel.vertx.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

public class JsonObjectUtils {
    /**
     * 将 JsonObject 对象参数化成 GET 请求的 queryParams 字符串
     * 如: name=black_neck&sex=male
     * 
     * @param params 表示 JsonObject 对象的参数
     * @return GET 请求的 queryParams 字符串
     */
    public static String parameterize(JsonObject params) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Object> entry : params) {
            if (!result.isEmpty()) {
                result.append("&");
            }
            String key   = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
            String value = URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8);
            result.append(key).append("=").append(value);
        }

        return result.toString();
    }

    /**
     * MultiMap 转 JsonObject
     * 
     * @param multiMap MultiMap的对象
     * @return JsonObject对象
     */
    public static JsonObject multiMapToJsonObject(MultiMap multiMap) {
        JsonObject jsonObject = new JsonObject();
        multiMap.forEach(entry -> jsonObject.put(entry.getKey(), entry.getValue()));
        return jsonObject;
    }
}
