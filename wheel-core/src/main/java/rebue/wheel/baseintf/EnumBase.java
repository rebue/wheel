package rebue.wheel.baseintf;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 枚举的基础接口<br>
 */
public interface EnumBase {

    /**
     * @return jackson序列化的值
     */
    @JsonValue // Jackson在序列化时，只序列化 @JsonValue 标注的值
    int getCode();

    String getName();

    String getDesc();

}
