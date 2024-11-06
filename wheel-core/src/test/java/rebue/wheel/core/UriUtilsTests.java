package rebue.wheel.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UriUtilsTests {

    @Test
    void addQueryParam() {
        Assertions.assertEquals("https://www.abc.com?a=%E5%BC%A0%E4%B8%89",
                UriUtils.addQueryParam("https://www.abc.com", "a", "张三"));
        Assertions.assertEquals("https://www.abc.com?a=%E5%BC%A0%E4%B8%89",
                UriUtils.addQueryParam("https://www.abc.com?a=%E6%9D%8E%E5%9B%9B", "a", "张三"));
        Assertions.assertEquals("https://www.abc.com?a=%E5%BC%A0%E4%B8%89&b=%E7%8E%8B%E4%BA%94",
                UriUtils.addQueryParam("https://www.abc.com?a=%E6%9D%8E%E5%9B%9B&b=%E7%8E%8B%E4%BA%94", "a", "张三"));
    }

}
