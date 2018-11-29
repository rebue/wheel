package rebue.wheel.dozer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dozermapper.core.DozerConverter;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.MapperAware;

public class MapConverter extends DozerConverter<Map<String, Object>, Object> implements MapperAware {

    private final static Logger      _log    = LoggerFactory.getLogger(MapConverter.class);

    private final Map<String, Field> _fields = new HashMap<>();

    public MapConverter(final Class<Map<String, Object>> prototypeA, final Class<Object> prototypeB) {
        super(prototypeA, prototypeB);
        for (final Field field : prototypeB.getDeclaredFields()) {
            field.setAccessible(true);       // 因为字段是private的，要设置为可访问
            _fields.put(field.getName(), field);
        }
    }

    @Override
    public void setMapper(final Mapper mapper) {
    }

    @Override
    public Object convertTo(final Map<String, Object> source, final Object destination) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        for (final Entry<String, Object> entry : source.entrySet()) {
            try {
                _fields.get(entry.getKey()).set(destination, entry.getValue());
            } catch (IllegalArgumentException | IllegalAccessException e) {
                _log.error("在利用dozer将Map中的每一项转成实例的字段时出现不应该的异常", e);
                e.printStackTrace();
            }
        }
        return destination;
    }

    @Override
    public Map<String, Object> convertFrom(final Object source, final Map<String, Object> destination) {
        if (source == null) {
            return null;
        }
        for (final Entry<String, Field> field : _fields.entrySet()) {
            try {
                destination.put(field.getKey(), field.getValue().get(source));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                _log.error("在利用dozer将实例的字段转成Map中的每一项时出现不应该的异常", e);
                e.printStackTrace();
            }
        }
        return destination;
    }

}
