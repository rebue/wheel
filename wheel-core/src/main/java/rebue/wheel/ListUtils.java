package rebue.wheel;

import java.util.List;

public class ListUtils {
    /**
     * 将List转成item1,item2,item3....的字符串格式
     */
    public static String toString(List<?> list) {
        String result = "";
        for (Object object : list) {
            result += object + ",";
        }
        result = StrUtils.delRight(result, 1);
        return result;
    }
}
