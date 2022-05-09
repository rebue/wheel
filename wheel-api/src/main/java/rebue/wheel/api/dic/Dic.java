package rebue.wheel.api.dic;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 枚举的基础接口
 */
public interface Dic {

    /**
     * @return jackson序列化的值
     */
    @JsonValue
    Integer getCode();  // Jackson在序列化时，只序列化 @JsonValue 标注的值

    String getName();

    String getDesc();

}
