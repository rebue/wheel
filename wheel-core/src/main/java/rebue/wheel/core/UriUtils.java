package rebue.wheel.core;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

        String hash = null;
        if (uri.contains("#")) {
            String[] uriSplit = uri.split("#");
            uri  = uriSplit[0];
            hash = uriSplit[1];
        }

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
                uri = baseUri + "?" + String.join("&", list);
            }
            // 没有该参数
            else {
                uri += "&" + queryParamPath;
            }
        }
        // 没有参数列表
        else {
            uri += "?" + queryParamPath;
        }

        if (hash != null) {
            uri += "#" + hash;
        }
        return uri;
    }

    /**
     * 将查询参数字符串转成Map对象
     * 
     * @param queryParams 查询参数
     * @return Map对象
     */
    public static Map<String, String> queryParamsToMap(String queryParams) {
        String[]            queryParamsSplit = queryParams.split("&");
        Map<String, String> map              = new HashMap<>();
        for (String queryParam : queryParamsSplit) {
            String[] queryParamSplit = queryParam.split("=");
            String   paramName       = queryParamSplit[0];
            String   paramValue      = queryParamSplit[1];
            map.put(paramName, paramValue);
        }
        return map;
    }

    /**
     * 将Map对象转成查询参数字符串
     *
     * @param map Map对象
     * @return 查询参数字符串
     */
    public static String mapToQueryParams(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!sb.isEmpty()) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return sb.toString();
    }

    /**
     * 添加多个查询参数
     *
     * @param uri         要替换查询参数的uri
     * @param queryParams 多个查询参数
     * @return 添加后的URI
     */
    public static String addQueryParams(String uri, String queryParams) {
        String hash = null;
        if (uri.contains("#")) {
            String[] uriSplit = uri.split("#");
            uri  = uriSplit[0];
            hash = uriSplit[1];
        }

        // 已有参数列表
        if (uri.contains("?")) {
            String[]            uriSplit             = uri.split("\\?");
            String              baseUri              = uriSplit[0];
            String              originQueryParams    = uriSplit[1];
            Map<String, String> queryParamsMap       = queryParamsToMap(queryParams);
            Map<String, String> originQueryParamsMap = queryParamsToMap(originQueryParams);
            originQueryParamsMap.putAll(queryParamsMap);
            uri = baseUri + "?" + mapToQueryParams(originQueryParamsMap);
        }
        // 没有参数列表
        else {
            uri += "?" + queryParams;
        }

        if (hash != null) {
            uri += "#" + hash;
        }
        return uri;
    }

}
