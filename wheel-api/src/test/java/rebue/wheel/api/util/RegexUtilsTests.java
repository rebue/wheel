package rebue.wheel.api.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RegexUtilsTests {
    @Test
    public void test01() {
        String text = """
                abc
                def
                ghi
                """;
        String firstLine = RegexUtils.findFirstLine(text);
        Assertions.assertEquals("abc", firstLine);
        text = "abc";
        firstLine = RegexUtils.findFirstLine(text);
        Assertions.assertEquals("abc", firstLine);
    }
}