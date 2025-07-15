package rebue.wheel.vertx.util;

import java.util.Map;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import rebue.wheel.core.UriUtils;

public class JsonObjectUtils {
    /**
     * 将 JsonObject 对象参数化成 GET 请求的 queryParams 字符串
     * 例如: name=black_neck&amp;sex=male
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
            String key   = UriUtils.encode(entry.getKey());
            String value = UriUtils.encode(entry.getValue().toString());
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
