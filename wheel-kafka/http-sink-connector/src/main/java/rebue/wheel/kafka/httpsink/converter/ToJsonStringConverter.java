package rebue.wheel.kafka.httpsink.converter;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.storage.Converter;

public class ToJsonStringConverter implements Converter {
    JsonConverter jsonConverter;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // 使用 Kafka Connect 的 JsonConverter 来处理 Struct 等复杂类型
        this.jsonConverter = new org.apache.kafka.connect.json.JsonConverter();
        Map<String, Object> converterConfig = new HashMap<>(configs);
        converterConfig.put("schemas.enable", false);         // 配置不包含 schema 信息
        jsonConverter.configure(converterConfig, false);

    }

    @Override
    public byte[] fromConnectData(String topic, Schema schema, Object value) {
        if (value == null) {
            return null;
        }

        try {
            return jsonConverter.fromConnectData(topic, schema, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert Connect data to JSON String", e);
        }
    }

    @Override
    public SchemaAndValue toConnectData(String topic, byte[] value) {
        if (value == null) {
            return SchemaAndValue.NULL;
        }

        try {
            // 将字节数组转换为字符串，然后解析为 JSON 结构
            String jsonString = new String(value, StandardCharsets.UTF_8);
            // 如果需要返回具体的 Schema 和值，可以根据 JSON 内容构建
            return new SchemaAndValue(Schema.STRING_SCHEMA, jsonString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON bytes to Connect data", e);
        }
    }
}
