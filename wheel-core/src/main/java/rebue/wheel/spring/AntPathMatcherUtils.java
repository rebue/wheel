package rebue.wheel.spring;

import java.util.List;

import org.springframework.util.AntPathMatcher;

public class AntPathMatcherUtils {
    private final static AntPathMatcher _matcher = new AntPathMatcher();
    static {
        _matcher.setCaseSensitive(false);
    }

    /**
     * 判断是否有请求的url是否匹配指定的pattern集合
     *
     * @param requestMethod 请求的方法
     * @param path          请求的路径
     * @param patterns      要匹配的pattern集合
     *
     * @return 只要有一个匹配就返回true
     */
    public static boolean anyMatch(final String requestMethod, final String path, final List<String> patterns) {
        return patterns.stream().anyMatch(pattern -> match(requestMethod, path, pattern));
    }

    /**
     * 判断是否有请求的url是否匹配指定的pattern集合
     *
     * @param requestMethod 请求的方法
     * @param path          请求的路径
     * @param patterns      要匹配的pattern集合
     *
     * @return 全部不匹配才返回false
     */
    public static boolean noneMatch(final String requestMethod, final String path, final List<String> patterns) {
        return patterns.stream().noneMatch(pattern -> match(requestMethod, path, pattern));
    }

    private static boolean match(final String requestMethod, final String path, final String pattern) {
        final String[] split = pattern.split(":");
        if (split.length == 1) {
            return _matcher.match(pattern, path);
        }
        else if (split.length == 2) {
            if ("*".equals(split[0]) || requestMethod.equals(split[0])) {
                return _matcher.match(split[1], path);
            }
            return false;
        }
        else {
            throw new RuntimeException("不支持此pattern: " + pattern);
        }
    }

}
