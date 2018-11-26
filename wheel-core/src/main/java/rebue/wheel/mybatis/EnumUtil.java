package rebue.wheel.mybatis;

import rebue.wheel.baseintf.EnumBase;

public class EnumUtil {
    public static <E extends Enum<?> & EnumBase> E codeOf(final Class<E> enumClass, final int code) {
        final E[] enumConstants = enumClass.getEnumConstants();
        for (final E e : enumConstants) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }
}
