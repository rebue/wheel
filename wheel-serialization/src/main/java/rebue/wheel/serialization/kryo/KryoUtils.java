package rebue.wheel.serialization.kryo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoUtils {
    static private final ThreadLocal<Kryo> kryos = ThreadLocal.withInitial(Kryo::new);

    public static byte[] writeObject(final Object obj) {
        if (obj == null) {
            return null;
        }
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream(); final Output output = new Output(baos)) {
            kryos.get().writeClassAndObject(output, obj);
            output.flush();
            return baos.toByteArray();
        } catch (final IOException e) {
            throw new RuntimeException("Kryo序列化出现异常", e);
        }
    }

    public static Object readObject(final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes); Input input = new Input(bais)) {
            return kryos.get().readClassAndObject(input);
        } catch (final IOException e) {
            throw new RuntimeException("Kryo反序列化出现异常", e);
        }
    }
}
