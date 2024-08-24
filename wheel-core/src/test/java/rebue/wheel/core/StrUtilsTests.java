package rebue.wheel.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StrUtilsTests {

    @Test
    void isCharCountOverLimit() {
        Assertions.assertTrue(StrUtils.isCharCountOverLimit("abcabcabcabcabc", 'b', 4));
        Assertions.assertFalse(StrUtils.isCharCountOverLimit("abcabcabcabcabc", 'b', 5));
        Assertions.assertFalse(StrUtils.isCharCountOverLimit("abcabcabcabcabc", 'b', 6));
    }

    @Test
    void count() {
        Assertions.assertEquals(StrUtils.count("abc", 'b'), 1);
        Assertions.assertEquals(StrUtils.count("abcabcabc", 'b'), 3);
        Assertions.assertEquals(StrUtils.count("abcabcabcabcabc", 'b'), 5);
    }
}
