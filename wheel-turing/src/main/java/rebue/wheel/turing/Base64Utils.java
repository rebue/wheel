package rebue.wheel.turing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.bouncycastle.util.encoders.Base64Encoder;

import sun.misc.BASE64Decoder;

/**
 * 
 * @since 1.8
 * @author zbz
 *
 */
public class Base64Utils {
    public static byte[] encode(byte[] data) throws IOException {
        Base64Encoder base64Encoder = new Base64Encoder();
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            base64Encoder.encode(data, 0, data.length, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }

    public static byte[] decode(String sData) throws IOException {
        Base64Encoder base64Encoder = new Base64Encoder();
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            base64Encoder.decode(sData, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }

    public static byte[] decode(byte[] data) throws IOException {
        Base64Encoder base64Encoder = new Base64Encoder();
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            base64Encoder.decode(data, 0, data.length, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }

    public static String encodeStr(byte[] data) throws IOException {
        return new String(encode(data));
    }

    public static String decodeStr(String sData) throws IOException {
        return new String(decode(sData));
    }

    public static byte[] decodeKey(String sKey) throws IOException {
        BASE64Decoder base64Decoder = new BASE64Decoder();
        return base64Decoder.decodeBuffer(sKey);
    }

    public static byte[] decodeCipherText(String sData) throws IOException {
        Base64Encoder base64Encoder = new Base64Encoder();
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            base64Encoder.decode(sData, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }
}
