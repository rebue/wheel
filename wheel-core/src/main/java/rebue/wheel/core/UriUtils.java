package rebue.wheel.core;

public class UriUtils {
    /**
     * 填补结束的斜杠
     * 如果最后一个字符已经是斜杠了，那么不会重复填补
     *
     * @param uri 要填补的uri
     * @return 填补好的uri字符串
     */
    public static String padEndSlash(String uri) {
        return uri.charAt(uri.length() - 1) == '/'
                ? uri + '/'
                : uri;
    }

    /**
     * 删除结束的斜杠
     *
     * @param uri 要删除斜杠的uri
     * @return 删除斜杠后的uri字符串
     */
    public static String removeEndSlash(String uri) {
        return uri.charAt(uri.length() - 1) == '/'
                ? uri.substring(0, uri.length() - 1)
                : uri;
    }
}
