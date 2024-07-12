package rebue.wheel.core;

import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipHelper {

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

    public static void main(String[] args) throws IOException {
        String originalData   = "Hello, this is some data to compress with gzip.中文";
        byte[] compressedData = compress(originalData);
        System.out.println("Compressed data: " + new String(compressedData));

        String decompressedData = decompress(compressedData);
        System.out.println("Decompressed data: " + decompressedData);
    }
}