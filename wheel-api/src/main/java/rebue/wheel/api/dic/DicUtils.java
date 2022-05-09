package rebue.wheel.api.dic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DicUtils {

    /**
     * 枚举缓存
     * 外面的Map是缓存所有字典类的集合，key为类名，value为字典类所有值的集合
     * 里面的Map是缓存该字典类所有值的集合，key为字典的code，value为字典的值
     */
    private static final Map<String, Map<Integer, Dic>> caches = new ConcurrentHashMap<>();

    /**
     * 获取字典类的所有值的集合
     */
    public static Map<Integer, Dic> getItems(final Class<?> dicClass) {
        if (!dicClass.isEnum()) {
            throw new IllegalArgumentException("参数必须是枚举类型");
        }

        if (!Dic.class.isAssignableFrom(dicClass)) {
            throw new IllegalArgumentException("参数必须实现Dic接口");
        }

        // 先从缓存中查找
        Map<Integer, Dic> items = caches.get(dicClass.getName());
        if (items != null) {
            return items;
        }

        // 如果缓存中没有，则利用反射获取
        items = Stream.of(dicClass.getEnumConstants()).map(item -> (Dic) item).collect(Collectors.toMap(Dic::getCode, item -> item));

        // 放入缓存
        caches.put(dicClass.getName(), items);

        return items;
    }

    /**
     * 根据code获取对应的字典值
     * 
     * @param dicClass 字典类型
     * @param code     字典的编码
     */
    public static Dic getItem(final Class<?> dicClass, final Integer code) {
        return getItems(dicClass).get(code);
    }

    /**
     * 判断code是否在字典中是有效的值
     * 
     * @param dicClass 字典类型
     * @param code     字典的编码
     */
    public static boolean isValid(final Class<?> dicClass, final Integer code) {
        return getItem(dicClass, code) != null;
    }
}
