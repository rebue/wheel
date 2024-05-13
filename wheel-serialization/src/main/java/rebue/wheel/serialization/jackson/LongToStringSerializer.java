/**
 * 复制com.fasterxml.jackson.databind.ser.std.ToStringSerializer jackson v2.15.2
 * 修改了valueToString方法添加双引号
 */
package rebue.wheel.serialization.jackson;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializerBase;

public class LongToStringSerializer extends ToStringSerializerBase {
    private static final long              serialVersionUID = 1L;

    /**
     * Singleton instance to use.
     */
    public final static ToStringSerializer instance         = new ToStringSerializer();

    /**
     * <p>
     * Note: usually you should NOT create new instances, but instead use
     * {@link #instance} which is stateless and fully thread-safe. However,
     * there are cases where constructor is needed; for example,
     * when using explicit serializer annotations like
     * {@link com.fasterxml.jackson.databind.annotation.JsonSerialize#using}.
     */
    public LongToStringSerializer() {
        super(Object.class);
    }

    /**
     * Sometimes it may actually make sense to retain actual handled type.
     *
     * @since 2.5
     */
    public LongToStringSerializer(Class<?> handledType) {
        super(handledType);
    }

    @Override
    public final String valueToString(Object value) {
        // XXX 添加双引号
        return "\"" + value.toString() + "\"";
    }

}
