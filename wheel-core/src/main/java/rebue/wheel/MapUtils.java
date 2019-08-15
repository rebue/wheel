package rebue.wheel;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public static String map2Str(final Map<String, ?> map) {
        final StringBuilder sb = new StringBuilder();
        for (final Entry<String, ?> item : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            String value = "";
            // request.getParameterMap()的返回值类型是Map<String,String[]>，因为像checkbox这样的组件会有一个name对应对个value的时候
            if (item.getValue().getClass().getName().equals("[Ljava.lang.String;")) {
                final String[] values = (String[]) item.getValue();
                for (final String val : values) {
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
    public static Map<?, ?> obj2Map(final Object obj) {
        if (obj == null) {
            return null;
        }

        return new BeanMap(obj);
    }

    /**
     * 将url参数("a=111&amp;b=222&amp;c=333")转换成map
     */
    public static Map<String, List<Object>> urlParams2Map(final String param) {
        final Map<String, List<Object>> map = new HashMap<>();
        if (StringUtils.isBlank(param)) {
            return map;
        }
        final String[] params = param.split("&");
        for (final String param2 : params) {
            final String[] p = param2.split("=");
            if (p.length == 2) {
                List<Object> list = map.get(p[0]);
                if (list == null) {
                    list = new ArrayList<>();
                }
                try {
                    list.add(URLDecoder.decode(p[1], "utf-8"));
                } catch (final UnsupportedEncodingException e) {
                    // 不会报的异常
                }
                map.put(p[0], list);
            }
        }
        return map;
    }

    /**
     * 将map转换成url参数("a=111&amp;b=222&amp;c=333")
     * 所有参数的值都进行URLEncoder的UTF-8编码
     * 
     */
    public static String map2UrlParams(final Map<String, List<Object>> map) {
        if (map == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (final Entry<String, List<Object>> entry : map.entrySet()) {
            for (final Object value : entry.getValue()) {
                sb.append(entry.getKey());
                sb.append("=");
                try {
                    if (value instanceof String) {
                        sb.append(URLEncoder.encode(String.valueOf(value), "utf-8"));
                    }
                } catch (final UnsupportedEncodingException e) {
                    throw new RuntimeException("不支持utf-8编码(不可能的)");
                }
                sb.append("&");
            }
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = StrUtils.delRight(s, 1);
        }
        return s;
    }

    /**
     * 将map转换成url参数("a=111&amp;b=222&amp;c=333")
     * 所有参数的值都进行URLEncoder的UTF-8编码
     * 
     */
    public static String map2UrlParams2(final Map<String, Object> map) {
        if (map == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (final Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey());
            sb.append("=");
            try {
                if (entry.getValue() instanceof String) {
                    sb.append(URLEncoder.encode(String.valueOf(entry.getValue()), "utf-8"));
                }
            } catch (final UnsupportedEncodingException e) {
                throw new RuntimeException("不支持utf-8编码(不可能的)");
            }
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = StrUtils.delRight(s, 1);
        }
        return s;
    }

    /**
     * 对map中的第一项的值进行url解码(一般用map接收请求参数时要用到)
     */
    public static void decodeUrl(final Map<String, Object> map) {
        _log.info("将请求参数进行url解码");
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof String) {
                try {
                    _log.debug("解码前:{}", entry.getValue());
                    entry.setValue(URLDecoder.decode(entry.getValue().toString(), "utf-8"));
                    _log.debug("解码后:{}", entry.getValue());
                } catch (final UnsupportedEncodingException e) {
                    throw new RuntimeException("不支持utf-8编码(不可能的)");
                }
            }
        }
    }

}
