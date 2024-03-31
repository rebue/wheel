package rebue.wheel.api.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class RegexUtilsTests {
    /**
     * 测试查找首行正则表达式
     */
    @Test
    public void test01_findFirstLine() {
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

    /**
     * 测试列出字典正则表达式
     */
    @Test
    public void test02_listDicRegex() {
        Pattern      DIC_REGEX = Pattern.compile("(\\d+)\\s*:\\s*(.+?)\\s*\\(\\s*(.+?)\\s*\\)");
        List<String> dices     = RegexUtils.listGroup(DIC_REGEX, "ABCabc");
        log.info("字典: {}", dices);
        Assertions.assertNull(dices);
        dices = RegexUtils.listGroup(DIC_REGEX, "1:GROUP(集团)");
        log.info("字典: {}", dices);
        Assertions.assertNotNull(dices);
        dices = RegexUtils.listGroup(DIC_REGEX, " 20:UNIT(政府单位)");
        log.info("字典: {}", dices);
        Assertions.assertNotNull(dices);
        dices = RegexUtils.listGroup(DIC_REGEX, "21 :CORP(公司)");
        log.info("字典: {}", dices);
        Assertions.assertNotNull(dices);
        dices = RegexUtils.listGroup(DIC_REGEX, "80: DEPT(部门)");
        log.info("字典: {}", dices);
        Assertions.assertNotNull(dices);
        dices = RegexUtils.listGroup(DIC_REGEX, " 90 : TEAM ( 小组 ) ");
        log.info("字典: {}", dices);
        Assertions.assertNotNull(dices);
    }
}