package rebue.wheel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StrUitlsTests {
    final String str = "含中英文及     符号 ,，ABC abc 123    ??？.。…………";

    /**
     * 测试获取含中英文及符号的字符串的长度
     */
    @Test
    public void test01() {
        final int length = str.length();
        assertTrue("错误：获取长度不正确", 39 == length);
    }

    /**
     * 测试截取含中英文及符号的字符串的子串
     */
    @Test
    public void test02() {
        final String substr = str.substring(0, 15);
        assertEquals("含中英文及     符号 ,，", substr);
    }

}
