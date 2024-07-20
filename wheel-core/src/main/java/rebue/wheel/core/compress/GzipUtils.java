package rebue.wheel.core.compress;

import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipUtils {

    @SneakyThrows
    public static byte[] compress(String data) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOut = new GZIPOutputStream(byteArrayOutputStream)) {
            gzipOut.write(data.getBytes());
        }
        return byteArrayOutputStream.toByteArray();
    }

    @SneakyThrows
    public static String decompress(byte[] compressedData) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);
        try (GZIPInputStream gzipIn = new GZIPInputStream(byteArrayInputStream)) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[]                buffer                = new byte[1024];
            int                   len;
            while ((len = gzipIn.read(buffer)) > 0) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            return byteArrayOutputStream.toString();
        }
    }

}