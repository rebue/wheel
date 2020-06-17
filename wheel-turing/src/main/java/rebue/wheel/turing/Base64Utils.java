package rebue.wheel.turing;

import java.io.IOException;
import java.util.Base64;

/**
 * @author zbz
 * @since 1.8
 */
public class Base64Utils {
    public static byte[] encode(byte[] data) throws IOException {
        return Base64.getEncoder().encode(data);
    }

    public static byte[] decode(String sData) throws IOException {
        return Base64.getDecoder().decode(sData);
    }

    public static byte[] decode(byte[] data) throws IOException {
        return Base64.getDecoder().decode(data);
    }

    public static String encodeStr(byte[] data) throws IOException {
        return new String(encode(data));
    }

    public static String decodeStr(String sData) throws IOException {
        return new String(decode(sData));
    }
}
