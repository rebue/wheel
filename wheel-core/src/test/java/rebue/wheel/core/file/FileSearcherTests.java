package rebue.wheel.core.file;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class FileSearcherTests {

    @Test
    public void test01() throws IOException {
        final String dirName = FileUtils.getProjectPath(); // 文件路径
        FileSearcher.searchFiles(dirName, ".*\\.java", file -> {
            try {
                System.out.println("test01 " + file.getCanonicalPath());
            } catch (final IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void test02() throws IOException {
        final String     dirName = FileUtils.getProjectPath(); // 文件路径
        final List<File> files   = FileSearcher.searchFiles(dirName, Pattern.compile(".*\\.java"));
        for (final File file : files) {
            System.out.println("test02 " + file.getCanonicalPath());
        }
    }
}