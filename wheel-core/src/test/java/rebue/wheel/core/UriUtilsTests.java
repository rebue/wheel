package rebue.wheel.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UriUtilsTests {

    @Test
    void testRemoveEndSlash() {
        Assertions.assertEquals("https://www.abc.com", UriUtils.removeEndSlash("https://www.abc.com"));
        Assertions.assertEquals("https://www.abc.com", UriUtils.removeEndSlash("https://www.abc.com/"));
    }

    @Test
    void testAddQueryParam() {
        Assertions.assertEquals("https://www.abc.com?a=%E5%BC%A0%E4%B8%89",
                UriUtils.addQueryParam("https://www.abc.com", "a", "张三"));
        Assertions.assertEquals("https://www.abc.com?a=%E5%BC%A0%E4%B8%89",
                UriUtils.addQueryParam("https://www.abc.com?a=%E6%9D%8E%E5%9B%9B", "a", "张三"));
        Assertions.assertEquals("https://www.abc.com?a=%E5%BC%A0%E4%B8%89&b=%E7%8E%8B%E4%BA%94",
                UriUtils.addQueryParam("https://www.abc.com?a=%E6%9D%8E%E5%9B%9B&b=%E7%8E%8B%E4%BA%94", "a", "张三"));
    }

    @Test
    void testAddQueryParams() {
        Assertions.assertEquals("https://www.abc.com?a=%E5%BC%A0%E4%B8%89",
                UriUtils.addQueryParams("https://www.abc.com", "a=%E5%BC%A0%E4%B8%89"));
        Assertions.assertEquals("https://www.abc.com?a=%E5%BC%A0%E4%B8%89&b=%E6%9D%8E%E5%9B%9B",
                UriUtils.addQueryParams("https://www.abc.com", "a=%E5%BC%A0%E4%B8%89&b=%E6%9D%8E%E5%9B%9B"));
        Assertions.assertEquals("https://www.abc.com?a=%E5%BC%A0%E4%B8%89&b=%E6%9D%8E%E5%9B%9B",
                UriUtils.addQueryParams("https://www.abc.com?a=%E6%9D%8E%E5%9B%9B", "a=%E5%BC%A0%E4%B8%89&b=%E6%9D%8E%E5%9B%9B"));
        Assertions.assertEquals("https://www.abc.com?a=%E5%BC%A0%E4%B8%89&b=%E6%9D%8E%E5%9B%9B&c=%E7%8E%8B%E4%BA%94",
                UriUtils.addQueryParams("https://www.abc.com?c=%E7%8E%8B%E4%BA%94", "a=%E5%BC%A0%E4%B8%89&b=%E6%9D%8E%E5%9B%9B"));
    }

}
