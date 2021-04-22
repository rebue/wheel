package rebue.wheel.serialization.fst;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class FstRedisSerializer implements RedisSerializer<Object> {

    @Override
    public byte[] serialize(final Object object) throws SerializationException {
        return FstUtils.writeObject(object);
    }

    @Override
    public Object deserialize(final byte[] bytes) throws SerializationException {
        return FstUtils.readObject(bytes);
    }

}
