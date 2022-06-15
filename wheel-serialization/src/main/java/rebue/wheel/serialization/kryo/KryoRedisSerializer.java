package rebue.wheel.serialization.kryo;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class KryoRedisSerializer implements RedisSerializer<Object> {

    @Override
    public byte[] serialize(final Object object) throws SerializationException {
        try {
            return KryoUtils.writeObject(object);
        } catch (final Exception e) {
            throw new SerializationException("Redis序列化异常", e);
        }
    }

    @Override
    public Object deserialize(final byte[] bytes) throws SerializationException {
        try {
            return KryoUtils.readObject(bytes);
        } catch (final Exception e) {
            throw new SerializationException("Redis反序列化异常", e);
        }
    }

}