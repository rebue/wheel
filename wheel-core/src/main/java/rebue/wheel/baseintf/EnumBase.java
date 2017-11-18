package rebue.wheel.baseintf;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 枚举的基础接口<br>
 */
public interface EnumBase {

    /**
     * @return jackson序列化的值
     */
    @JsonValue
    int getCode();

}
