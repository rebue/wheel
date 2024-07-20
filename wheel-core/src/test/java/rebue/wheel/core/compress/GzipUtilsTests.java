package rebue.wheel.core.compress;

import org.junit.jupiter.api.Test;

public class GzipUtilsTests {
    @Test
    public void test01() {
        String originalData   = "Hello, this is some data to compress with gzip.中文";
        byte[] compressedData = GzipUtils.compress(originalData);
        System.out.println("Compressed data: " + new String(compressedData));

        String decompressedData = GzipUtils.decompress(compressedData);
        System.out.println("Decompressed data: " + decompressedData);
    }

}
