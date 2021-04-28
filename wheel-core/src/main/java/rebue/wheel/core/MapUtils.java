package rebue.wheel.core;

import java.beans.IntrospectionException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MapUtils {
    private static Mapper _dozerMapper = DozerBeanMapperBuilder.buildDefault();

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
            // request.getParameterMap()的返回值类型是Map<String,String[]>，因为像checkbox这样的组件会有一个name对应多个value的时候
            if (item.getValue().getClass().getName().equals("[Ljava.lang.String;")) {
                final String[] values = (String[]) item.getValue();
                for (final String val : values) {
                    value += val + ",";
                }
                value = "[" + StrUtils.left(value, value.length() - 1) + "]";
            }
            else {
                value = item.getValue().toString();
            }
            sb.append(item.getKey() + ":" + value);
        }
        return sb.toString();
    }

    /**
     * 将map转成Bean对象
     */
    public static Object map2Bean(final Map<String, Object> map, final Class<?> beanClass) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        if (map == null || map.isEmpty()) {
            return null;
        }

        // final Object bean = beanClass.newInstance();
        // 利用org.apache.commons.beanutils.BeanUtils转换
        // BeanUtils.populate(bean, map);

        // 利用Dozer转换
        final Object bean = _dozerMapper.map(map, beanClass);
        return bean;
    }

    /**
     * 将Bean对象转成map
     */
    public static Map<?, ?> bean2Map(final Object bean) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException {
        if (bean == null) {
            return null;
        }

        // 此方法会多一个名为 class 的键值对
        // return new BeanMap(bean);

        // 利用dozer转换
        return _dozerMapper.map(bean, Map.class);

        // final Map<String, Object> map = new HashMap<>();
        //
        // final BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
        // final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        // for (final PropertyDescriptor property : propertyDescriptors) {
        // final String key = property.getName();
        // if (key.compareToIgnoreCase("class") == 0) {
        // continue;
        // }
        // final Method getter = property.getReadMethod();
        // final Object value = getter != null ? getter.invoke(bean) : null;
        // map.put(key, value);
        // }
        //
        // return map;
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
    public static String map2UrlParams(final Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        final StringJoiner sj = new StringJoiner("&");
        map.forEach((key, value) -> {
            if (value instanceof List) {
                ((List<?>) value).forEach(item -> {
                    try {
                        sj.add(key + "=" + URLEncoder.encode(item.toString(), "utf-8"));
                    } catch (final UnsupportedEncodingException e) {
                        throw new RuntimeException("不支持utf-8编码(不可能的)");
                    }
                });
            }
            else {
                try {
                    sj.add(key + "=" + URLEncoder.encode(value.toString(), "utf-8"));
                } catch (final UnsupportedEncodingException e) {
                    throw new RuntimeException("不支持utf-8编码(不可能的)");
                }
            }
        });
        return sj.toString();
    }

    /**
     * 对map中的第一项的值进行url解码(一般用map接收请求参数时要用到)
     */
    public static void decodeUrl(final Map<String, Object> map) {
        log.info("将请求参数进行url解码");
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof String) {
                try {
                    log.debug("解码前:{}", entry.getValue());
                    entry.setValue(URLDecoder.decode(entry.getValue().toString(), "utf-8"));
                    log.debug("解码后:{}", entry.getValue());
                } catch (final UnsupportedEncodingException e) {
                    throw new RuntimeException("不支持utf-8编码(不可能的)");
                }
            }
        }
    }

}
