package rebue.wheel;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapUtils {
    private static final Logger _log = LoggerFactory.getLogger(MapUtils.class);

    /**
     * 将map转换为string(a:1,b:2,c:3)
     */
    public static String map2Str(Map<String, ?> map) {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, ?> item : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            String value = "";
            // request.getParameterMap()的返回值类型是Map<String,String[]>，因为像checkbox这样的组件会有一个name对应对个value的时候
            if (item.getValue().getClass().getName().equals("[Ljava.lang.String;")) {
                String[] values = (String[]) item.getValue();
                for (String val : values) {
                    value += val + ",";
                }
                value = "[" + StrUtils.left(value, value.length() - 1) + "]";
            } else {
                value = item.getValue().toString();
            }
            sb.append(item.getKey() + ":" + value);
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
     * 将url参数("a=111&amp;b=222&amp;c=333")转换成map
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
     * 将map转换成url参数("a=111&amp;b=222&amp;c=333")
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

    /**
     * 对map中的第一项的值进行url解码(一般用map接收请求参数时要用到)
     */
    public static void decodeUrl(Map<String, Object> map) {
        _log.info("将请求参数进行url解码");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof String) {
                try {
                    _log.debug("解码前:{}", entry.getValue());
                    entry.setValue(URLDecoder.decode(entry.getValue().toString(), "utf-8"));
                    _log.debug("解码后:{}", entry.getValue());
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("不支持utf-8编码(不可能的)");
                }
            }
        }
    }

}
