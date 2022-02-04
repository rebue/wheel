package rebue.wheel.turing;

import java.util.zip.CRC32;

public class CrcUtils {

    public static String crc32(final String data) {
        final CRC32 crc32 = new CRC32();
        crc32.update(data.getBytes(), 0, data.length());
        return String.valueOf(crc32.getValue());
    }

}
