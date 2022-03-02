package rebue.wheel.turing;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author zbz
 *
 * @since 1.8
 */
public class Base64Utils {
    public static byte[] encode(final String data) {
        return encode(data.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] encode(final byte[] data) {
        return Base64.getEncoder().encode(data);
    }

    public static String encodeStr(final byte[] data) {
        return new String(encode(data), StandardCharsets.UTF_8);
    }

    public static String encodeStr(final String data) {
        return new String(encode(data), StandardCharsets.UTF_8);
    }

    public static byte[] decode(final String data) {
        return decode(data.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] decode(final byte[] data) {
        return Base64.getDecoder().decode(data);
    }

    public static String decodeStr(final String data) {
        return decodeStr(data.getBytes(StandardCharsets.UTF_8));
    }

    public static String decodeStr(final byte[] data) {
        return new String(decode(data), StandardCharsets.UTF_8);
    }

}
