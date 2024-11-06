package rebue.wheel.core;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class UriUtils {
    /**
     * 填补结束的斜杠
     * 如果最后一个字符已经是斜杠了，那么不会重复填补
     *
     * @param uri 要填补的uri
     * @return 填补好的uri字符串
     */
    public static String padEndSlash(String uri) {
        return uri.charAt(uri.length() - 1) == '/' ? uri : uri + '/';
    }

    /**
     * 删除结束的斜杠
     *
     * @param uri 要删除斜杠的uri
     * @return 删除斜杠后的uri字符串
     */
    public static String removeEndSlash(String uri) {
        return uri.charAt(uri.length() - 1) == '/' ? uri.substring(0, uri.length() - 1) : uri;
    }

    /**
     * 添加查询参数
     * 
     * @param uri             要替换查询参数的uri
     * @param queryParamName  查询参数的名称
     * @param queryParamValue 查询参数的值
     * @return 添加后的URI
     */
    public static String addQueryParam(String uri, String queryParamName, String queryParamValue) {
        String queryParamPath = queryParamName + "=" + URLEncoder.encode(queryParamValue, StandardCharsets.UTF_8);
        // 已有参数列表
        if (uri.contains("?")) {
            // 已有该参数
            if (uri.contains(queryParamName + "=")) {
                String[]     uriSplit         = uri.split("\\?");
                String       baseUri          = uriSplit[0];
                String       queryParams      = uriSplit[1];
                String[]     queryParamsSplit = queryParams.split("&");
                List<String> list             = new LinkedList<>();
                for (String queryParam : queryParamsSplit) {
                    String[] queryParamSplit = queryParam.split("=");
                    String   paramName       = queryParamSplit[0];
                    // String paramValue = queryParamSplit[1];
                    if (paramName.equals(queryParamName)) {
                        list.add(queryParamPath);
                    } else {
                        list.add(queryParam);
                    }
                }
                return baseUri + "?" + String.join("&", list);
            }
            // 没有该参数
            else {
                queryParamPath = "&" + queryParamPath;
            }
        }
        // 没有参数列表
        else {
            queryParamPath = "?" + queryParamPath;
        }
        return uri + queryParamPath;
    }
}
