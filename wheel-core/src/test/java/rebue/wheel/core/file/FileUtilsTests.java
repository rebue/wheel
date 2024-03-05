package rebue.wheel.core.file;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class FileUtilsTests {

    @Test
    void testIsAbsPath() {
        Assertions.assertFalse(FileUtils.isAbsPath("./"));
    }
}
