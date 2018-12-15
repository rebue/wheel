package test;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import entity.PojoEntity;
import rebue.wheel.test.MockDataUtils;

public class MockDataUtilsTest {

	@Test
	public void test() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		PojoEntity entity = (PojoEntity) MockDataUtils.newRandomPojo(new PojoEntity().getClass());
		System.out.println(entity);
	}
}
