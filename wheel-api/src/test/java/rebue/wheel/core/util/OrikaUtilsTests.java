package rebue.wheel.api;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;
import rebue.wheel.api.entity.PojoEntity;

@Slf4j
public class OrikaUtilsTests {

    @Test
    public void test01() {
        final PojoEntity pojoEntity = new PojoEntity();
        pojoEntity.setId(1L);
        pojoEntity.setAge((byte) 11);
        pojoEntity.setIdCard("safsadfdsafasf");
        pojoEntity.setName("sadfsadf");
        pojoEntity.setPhone1(1111111.1);
        pojoEntity.setPhone2(2222222);
        pojoEntity.setPrice(BigDecimal.valueOf(11111111.111));
        pojoEntity.setStudentCode("sadfasdfsdaf");

        final Map<String, Object> map = OrikaUtils.mapAsMap(pojoEntity);
        log.info("map: {}", map);
    }

}
