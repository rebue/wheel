package rebue.wheel.test;

import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:**/spring-*.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class AbstractSpringContextTest extends AbstractJUnit4SpringContextTests {

}
