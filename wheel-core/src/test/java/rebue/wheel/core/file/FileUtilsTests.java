package rebue.wheel.core.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileUtilsTests {

    @Test
    void testIsAbsPath() {
        Assertions.assertFalse(FileUtils.isAbsPath("./"));
    }
}
