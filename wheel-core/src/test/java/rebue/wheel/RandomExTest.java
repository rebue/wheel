package rebue.wheel;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import entity.PojoEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class RandomExTest {

    @Test
    @Disabled
    public void test01() throws NoSuchAlgorithmException, NoSuchProviderException {
        Random              random;
        final long          start   = System.currentTimeMillis();
        final List<Integer> randoms = new ArrayList<>();
        int                 id;
        for (int i = 0; i < 10; i++) {
            random = RandomEx.getRandom();
            id     = random.nextInt(Integer.MAX_VALUE);
            // log.info(random.nextInt(Integer.MAX_VALUE));
            randoms.add(id);
        }
        final long end = System.currentTimeMillis();
        log.info(String.format("耗时%d毫秒", end - start));
        printDistribution("Random", randoms);
    }// test01

    private void printDistribution(final String name, final List<Integer> frequencies) {
        System.out.printf("%n%s distribution |8000     |9000     |10000    |11000    |12000%n", name);
        for (int i = 0; i < 10; i++) {
            final char[] bar = "                                                  ".toCharArray(); // 50 chars.
            Arrays.fill(bar, 0, Math.max(0, Math.min(50, frequencies.get(i) / 100 - 80)), '#');
            System.out.printf("0.%dxxx: %6d  :%s%n", i, frequencies.get(i), new String(bar));
        }
    }

    /**
     * 测试随机生成汉字
     */
    @Test
    @Disabled
    public void testRandomCn() {
        log.info(String.valueOf(RandomEx.randomCnChar()));
        log.info(RandomEx.randomCnStr(1000));
    }

    @Test
    @Disabled
    public void testRandomBoolean() {
        for (int i = 0; i < 100; i++) {
            log.info(String.valueOf(RandomEx.randomBoolean()));
        }
    }

    @Test
    @Disabled
    public void testRandomDate() {
        for (int i = 0; i < 100; i++) {
            log.info(String.valueOf(RandomEx.randomDate()));
        }
    }

    @Test
    @Disabled
    public void testRandomMobile() {
        for (int i = 0; i < 100; i++) {
            log.info(RandomEx.randomMobile());
        }
    }

    @Test
    @Disabled
    public void testRandomEmail() {
        for (int i = 0; i < 100; i++) {
            log.info(RandomEx.randomEmail());
        }
    }

    @Test
//    @Disabled
    public void testRandomIdCard() {
        for (int i = 0; i < 100; i++) {
            log.info(RandomEx.randomIdCard());
        }
    }

    @Test
    @Disabled
    public void testRandomAddress() {
        for (int i = 0; i < 100; i++) {
            log.info(RandomEx.randomAddress());
        }
    }

    @Test
    @Disabled
    public void testRandomChineseName() {
        for (int i = 0; i < 100; i++) {
            log.info(RandomEx.randomChineseName());
        }
    }

    /**
     * 测试生成随机属性值的对象
     */
    @Test
    @Disabled
    public void testRandomPojo() throws ReflectiveOperationException {
        log.info(RandomEx.randomPojo(new PojoEntity().getClass()).toString());
    }
}
