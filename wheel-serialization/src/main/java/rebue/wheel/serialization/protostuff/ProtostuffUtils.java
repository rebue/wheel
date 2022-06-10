package rebue.wheel.serialization.protostuff;

import java.util.Map;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class ProtostuffUtils {

    public static <T> byte[] serialize(final T obj) {
        final LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            if (obj instanceof Map) {
                final Wrapper<T>         wrapper = new Wrapper<>(obj);
                @SuppressWarnings("unchecked")
                final Class<Wrapper<T>>  cls     = (Class<Wrapper<T>>) wrapper.getClass();
                final Schema<Wrapper<T>> schema  = RuntimeSchema.getSchema(cls);
                final byte[]             result  = ProtobufIOUtil.toByteArray(new Wrapper<>(obj), schema, buffer);
                return result;
            }
            else {
                @SuppressWarnings("unchecked")
                final Class<T>  cls    = (Class<T>) obj.getClass();
                final Schema<T> schema = RuntimeSchema.getSchema(cls);
                // return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
                final byte[]    result = ProtobufIOUtil.toByteArray(obj, schema, buffer);
                return result;
            }
        } finally {
            buffer.clear();
        }
    }

    public static <T> T deserialize(final byte[] data, final Class<T> cls) {
        if (cls.isAssignableFrom(Map.class)) {
            @SuppressWarnings("rawtypes")
            final Schema<Wrapper> schema  = RuntimeSchema.getSchema(Wrapper.class);
            @SuppressWarnings("unchecked")
            final Wrapper<T>      message = schema.newMessage();
            ProtobufIOUtil.mergeFrom(data, message, schema);
            return message.getInner();
        }
        else {
            final Schema<T> schema  = RuntimeSchema.getSchema(cls);
            final T         message = schema.newMessage();
            // ProtostuffIOUtil.mergeFrom(data, message, schema);
            ProtobufIOUtil.mergeFrom(data, message, schema);
            return message;
        }
    }
}
