package rebue.wheel;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import rebue.wheel.test.AbstractSpringContextTest;

public class RandomExTest extends AbstractSpringContextTest {

	@Test
	public void test01() throws NoSuchAlgorithmException, NoSuchProviderException {
		Random random;
		long start = System.currentTimeMillis();
		List<Integer> randoms = new ArrayList<Integer>();
		int id;
		for (int i = 0; i < 10; i++) {
			random = RandomEx.getRandom();
			id = random.nextInt(Integer.MAX_VALUE);
			// System.out.println(random.nextInt(Integer.MAX_VALUE));
			randoms.add(id);
		}
		long end = System.currentTimeMillis();
		System.out.println(String.format("耗时%d毫秒", end - start));
		printDistribution("Random", randoms);
	}// test01

	private void printDistribution(String name, List<Integer> frequencies) {
		System.out.printf("%n%s distribution |8000     |9000     |10000    |11000    |12000%n", name);
		for (int i = 0; i < 10; i++) {
			char[] bar = "                                                  ".toCharArray(); // 50 chars.
			Arrays.fill(bar, 0, Math.max(0, Math.min(50, frequencies.get(i) / 100 - 80)), '#');
			System.out.printf("0.%dxxx: %6d  :%s%n", i, frequencies.get(i), new String(bar));
		}
	}
}
