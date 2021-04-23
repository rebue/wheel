package rebue.wheel.serialization.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoUtils {
    /**
     * FIXME kryo非线程安全
     */
    private static Kryo _kryo = new Kryo();

    public static byte[] writeObject(final Object obj) {
        if (obj == null) {
            return null;
        }
        try (final Output output = new Output()) {
            _kryo.writeClassAndObject(output, obj);
            return output.toBytes();
        }

        // try (final ByteArrayOutputStream baos = new ByteArrayOutputStream(); final Output output = new Output(baos)) {
        // _kryo.writeClassAndObject(output, obj);
        // output.flush();
        // return baos.toByteArray();
        // }
    }

    public static Object readObject(final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try (Input input = new Input()) {
            return _kryo.readClassAndObject(input);
        }
        // try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes); Input input = new Input(bais)) {
        // return _kryo.readClassAndObject(input);
        // }
    }
}
