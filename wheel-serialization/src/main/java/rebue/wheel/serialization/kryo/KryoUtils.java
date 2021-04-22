package rebue.wheel.serialization.kryo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoUtils {
    private static Kryo _kryo = new Kryo();

    public static byte[] writeObject(final Object obj) throws IOException {
        if (obj == null) {
            return null;
        }
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream(); final Output output = new Output(baos)) {
            _kryo.writeClassAndObject(output, obj);
            output.flush();
            return baos.toByteArray();
        }
    }

    public static Object readObject(final byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes); Input input = new Input(bais)) {
            return _kryo.readClassAndObject(input);
        }
    }
}
