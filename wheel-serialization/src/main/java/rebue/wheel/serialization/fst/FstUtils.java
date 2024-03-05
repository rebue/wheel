package rebue.wheel.serialization.fst;

import org.nustaq.serialization.FSTConfiguration;

public class FstUtils {
    static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    public static byte[] writeObject(final Object obj) {
        if (obj == null) {
            return null;
        }
        return conf.asByteArray(obj);
    }

    public static Object readObject(final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return conf.asObject(bytes);
    }

}
