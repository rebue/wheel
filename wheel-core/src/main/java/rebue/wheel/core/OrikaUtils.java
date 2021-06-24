package rebue.wheel.core;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

public class OrikaUtils {
    /**
     * 克隆工具，可以进行复杂和深度的克隆
     */
    public static final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

}
