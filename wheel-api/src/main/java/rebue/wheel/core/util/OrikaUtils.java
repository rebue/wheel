package rebue.wheel.core.util;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import java.util.Map;

public class OrikaUtils {
    /**
     * 克隆工具，可以进行复杂和深度的克隆
     */
    public final static MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    /**
     * 克隆对象
     *
     * @param <S>              源对象类型
     * @param <D>              目的对象类型
     * @param sourceObject     源对象
     * @param destinationClass 目的对象类型
     */
    public final static <S, D> D map(final S sourceObject, final Class<D> destinationClass) {
        return OrikaUtils.mapperFactory.getMapperFacade().map(sourceObject, destinationClass);
    }

    /**
     * 映射对象属性
     *
     * @param <S>               源对象类型
     * @param <D>               目的对象类型
     * @param sourceObject      源对象
     * @param destinationObject 目的对象
     */
    public final static <S, D> void map(final S sourceObject, final D destinationObject) {
        OrikaUtils.mapperFactory.getMapperFacade().map(sourceObject, destinationObject);
    }

    /**
     * 映射到map
     *
     * @param source 源对象
     */
    @SuppressWarnings("unchecked")
    public final static Map<String, Object> mapToMap(final Object sourceObject) {
        return OrikaUtils.mapperFactory.getMapperFacade().map(sourceObject, Map.class);
    }
}
