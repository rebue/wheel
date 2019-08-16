package rebue.wheel;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PojoUtils {
    static {
        ConvertUtils.register(new Converter() {
            @SuppressWarnings("unchecked")
            @Override
            public <T> T convert(final Class<T> type, final Object value) {
                if (value == null) {
                    return null;
                }
                log.debug("转换{}类型成Date类型:{}", value.getClass().getName(), value);
                if ("java.lang.String".equals(value.getClass().getName())) {
                    try {
                        return (T) DateUtils.stringToDate((String) value);
                    } catch (final ParseException e) {
                        throw new RuntimeException("Map转POJO对象时出错", e);
                    }
                }
                throw new RuntimeException("不支持" + value.getClass().getName() + "类型转换为java.util.Date类型");
            }

        }, Date.class);
    }

    /**
     * Map转POJO对象
     */
    public static <POJO> POJO mapToPojo(final Map<String, Object> map, final Class<POJO> pojoClass) {
        if (map == null) {
            return null;
        }
        try {
            final POJO obj = pojoClass.newInstance();
            org.apache.commons.beanutils.BeanUtils.populate(obj, map);
            return obj;
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("不会发生的异常", e);
        }
    }

    /**
     * POJO对象转Map
     */
    public static <POJO> Map<?, ?> pojoToMap(final POJO pojo) {
        if (pojo == null) {
            return null;
        }
        return new org.apache.commons.beanutils.BeanMap(pojo);
    }

}
