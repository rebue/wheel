package rebue.wheel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.StringUtils;

public class MapUtils {
    /**
     * 将map转换为string(a:1,b:2,c:3)
     */
    public static String map2Str(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Object> item : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(item.getKey() + ":" + item.getValue());
        }
        return sb.toString();
    }

    /**
     * 将对象的属性和值转成map
     */
    public static Map<?, ?> obj2Map(Object obj) {
        if (obj == null)
            return null;

        return new BeanMap(obj);
    }

    /**
     * 将url参数(a=1&b=2&c=3)转换成map
     */
    public static Map<String, Object> urlParams2Map(String param) {
        Map<String, Object> map = new HashMap<String, Object>(0);
        if (StringUtils.isBlank(param)) {
            return map;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            }
        }
        return map;
    }

    /**
     * 将map转换成url参数(a=1&b=2&c=3)
     * 所有参数的值都进行URLEncoder的UTF-8编码
     * 
     */
    public static String map2UrlParams(Map<String, Object> map) {
        if (map == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey());
            sb.append("=");
            try {
                sb.append(URLEncoder.encode(entry.getValue().toString(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("不支持utf-8编码(不可能的)");
            }
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = StringUtils.substringBeforeLast(s, "&");
        }
        return s;
    }

}
