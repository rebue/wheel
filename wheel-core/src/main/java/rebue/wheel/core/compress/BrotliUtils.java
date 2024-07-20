package rebue.wheel.core.compress;

import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.aayushatharva.brotli4j.decoder.Decoder;
import com.aayushatharva.brotli4j.encoder.Encoder;

import lombok.SneakyThrows;

public class BrotliUtils {
    static {
        Brotli4jLoader.ensureAvailability();
    }

    @SneakyThrows
    public static byte[] compress(String data) {
        return Encoder.compress(data.getBytes());

    }

    @SneakyThrows
    public static String decompress(byte[] compressedData) {
        return new String(Decoder.decompress(compressedData).getDecompressedData());
    }

}
