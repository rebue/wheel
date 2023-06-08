package rebue.wheel.serialization.protostuff;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProtostuffUtilsTests {

    @Test
    public void test01() {
        final Student student1 = new Student(1L, "N001", "张三", (short) 28, new Date());
        final byte[]  data     = ProtostuffUtils.serialize(student1);
        final Student student2 = ProtostuffUtils.deserialize(data, Student.class);
        Assertions.assertEquals(student1, student2);
    }

    @Test
    public void test02() {
        final Date                now  = new Date();
        final Map<String, Object> map1 = new LinkedHashMap<>();
        map1.put("a", "a");
        map1.put("b", 1);
        map1.put("c", true);
        map1.put("d", now);
        final byte[]                                             data = ProtostuffUtils.serialize(map1);
        @SuppressWarnings("unchecked") final Map<String, Object> map2 = ProtostuffUtils.deserialize(data, Map.class);
        Assertions.assertEquals(map1, map2);
    }

}
