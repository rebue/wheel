package rebue.wheel.baseintf;

/**
 * 枚举的基础接口<br>
 */
public interface BaseEnum {
    int getValue();

    default String toStr() {
        return Integer.toString(getValue());
    }
}
