package test;

import org.junit.Test;

import entity.PojoEntity;
import rebue.wheel.test.MockDataUtils;

public class MockDataUtilsTest {

    @Test
    public void test() throws ReflectiveOperationException {
        System.out.println(MockDataUtils.newRandomPojo(new PojoEntity().getClass()));
    }
}
