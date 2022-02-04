package rebue.wheel.turing;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CrcUtilsTest {

    @Test
    public void testCrc() {
        final String data = "ABCabc123";
        log.info("CRC: " + data + " " + CrcUtils.crc32(data));
    }

}
