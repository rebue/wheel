package rebue.wheel.turing;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Base64;

/**
 * @author zbz
 *
 * @since 1.8
 */
public class Base64Utils {
    public static byte[] encode(final byte[] data) throws IOException {
        return Base64.getEncoder().encode(data);
    }

    public static byte[] decode(final String sData) throws IOException {
        return Base64.getDecoder().decode(sData);
    }

    public static byte[] decode(final byte[] data) throws IOException {
        return Base64.getDecoder().decode(data);
    }

    public static String encodeStr(final byte[] data) throws IOException {
        return new String(encode(data));
    }

    public static String decodeStr(final String sData) throws IOException {
        return new String(decode(sData));
    }

    public static String encodeUrl(final byte[] data) throws IOException {
        return URLEncoder.encode(new String(encode(data)), "utf-8");
    }

}
