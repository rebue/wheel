package rebue.wheel.serialization.jackson;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JacksonUtilsTests {
    @Test
    public void test01() throws IOException {
        Student student = new Student(1L, "N001", "张三", (short) 28, new Date());
        log.info("student serialize: {}", JacksonUtils.serialize(student));
        student = JacksonUtils.deserialize(
                "{\"id\":1,\"num\":\"N001\",\"name\":\"张三\",\"age\":28,\"birthDate\":\"2022-11-03T11:33:47.037+08:00\"}",
                Student.class);
        log.info("student deserialize: {}", student);
        student = JacksonUtils.deserialize(
                "{\"id\":1,\"num\":\"N001\",\"name\":\"张三\",\"age\":28,\"birth_date\":\"2022-11-03T11:33:47.037+08:00\"}",
                Student.class);
        log.info("student deserialize: {}", student);
    }

    @Test
    public void testMap() throws IOException {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("a", "AAA");
        map.put("b", "BBB");
        map.put("c", "CCC");
        map.put("zs", "张三");
        map.put("李四", "李四");
        log.info("map: {}", JacksonUtils.serialize(map));
    }
}
