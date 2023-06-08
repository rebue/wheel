package rebue.wheel.serialization.jackson;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;

@Slf4j
public class JsonNamingTests {
    @Test
    public void test01() throws IOException {
        Student student = new Student(1L, "N001", "张三", (short) 28, new Date());
        log.info("student serialize: {}", JacksonUtils.serialize(student));
        student = JacksonUtils.deserialize("{\"id\":1,\"num\":\"N001\",\"name\":\"张三\",\"age\":28,\"birthDate\":\"2022-11-03T11:33:47.037+08:00\"}", Student.class);
        log.info("student deserialize: {}", student);
        student = JacksonUtils.deserialize("{\"id\":1,\"num\":\"N001\",\"name\":\"张三\",\"age\":28,\"birth_date\":\"2022-11-03T11:33:47.037+08:00\"}", Student.class);
        log.info("student deserialize: {}", student);
    }
}
