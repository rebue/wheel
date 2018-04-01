package rebue.wheel;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanMap;

public class MapUtils {
    public static String map2Str(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Object> item : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(item.getKey() + ":" + item.getValue());
        }
        return sb.toString();
    }

    public static Map<?, ?> obj2Map(Object obj) {
        if (obj == null)
            return null;

        return new BeanMap(obj);
    }

}
