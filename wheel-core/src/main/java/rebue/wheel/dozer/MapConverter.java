package rebue.wheel.dozer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapConverter extends DozerConverter<Map<String, Object>, Object> implements MapperAware {

    private final static Logger _log    = LoggerFactory.getLogger(MapConverter.class);

    private Map<String, Field>  _fields = new HashMap<>();

    public MapConverter(Class<Map<String, Object>> prototypeA, Class<Object> prototypeB) {
        super(prototypeA, prototypeB);
        for (Field field : prototypeB.getDeclaredFields()) {
            field.setAccessible(true);       // 因为字段是private的，要设置为可访问
            _fields.put(field.getName(), field);
        }
    }

    @Override
    public void setMapper(Mapper mapper) {
    }

    @Override
    public Object convertTo(Map<String, Object> source, Object destination) {
        if (source == null || source.isEmpty())
            return null;
        for (Entry<String, Object> entry : source.entrySet()) {
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
    public Map<String, Object> convertFrom(Object source, Map<String, Object> destination) {
        if (source == null)
            return null;
        for (Entry<String, Field> field : _fields.entrySet()) {
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
