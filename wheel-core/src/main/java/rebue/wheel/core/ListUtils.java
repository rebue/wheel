package rebue.wheel.core;

import java.util.List;
import java.util.stream.Collectors;

public class ListUtils {
    /**
     * 将List转成item1,item2,item3....的字符串格式
     */
    public static String toString(final List<?> list) {
        // String result = "";
        // for (Object object : list) {
        // result += object + ",";
        // }
        // if (result.length() > 0)
        // result = StrUtils.delRight(result, 1);
        // return result;
        return list.stream().map(Object::toString).collect(Collectors.joining(","));
    }
}