package rebue.wheel.core.file;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class FileSearcherTests {

    @Test
    public void test01() throws IOException {
        final String dirName = FileUtils.getProjectPath(); // 文件路径
        FileSearcher.searchFiles(dirName, ".*\\.java", file -> {
            try {
                System.out.println("test01 " + file.getCanonicalPath());
            } catch (final IOException e) {
                log.error("test01", e);
            }
        });
    }

    @Test
    public void test02() throws IOException {
        final File dirName = new File(FileUtils.getProjectPath()); // 文件路径
        FileSearcher.searchFiles(dirName, ".*\\.java", file -> {
            try {
                System.out.println("test01 " + file.getCanonicalPath());
            } catch (final IOException e) {
                log.error("test02", e);
            }
        });
    }

    @Test
    public void test03() throws IOException {
        final String     dirName = FileUtils.getProjectPath(); // 文件路径
        final List<File> files   = FileSearcher.findFiles(dirName, Pattern.compile(".*\\.java"));
        for (final File file : files) {
            System.out.println("test03 " + file.getCanonicalPath());
        }
    }
}