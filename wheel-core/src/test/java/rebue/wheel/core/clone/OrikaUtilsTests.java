package rebue.wheel.core.clone;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import rebue.wheel.core.OrikaUtils;

import java.math.BigDecimal;
import java.util.Map;

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

        @SuppressWarnings("deprecation") final Map<String, Object> map = OrikaUtils.mapToMap(pojoEntity);
        log.info("map: {}", map);
    }

}