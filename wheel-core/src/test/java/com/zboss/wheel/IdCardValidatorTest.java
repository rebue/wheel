package com.zboss.wheel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zboss.wheel.test.AbstractSpringContextTest;

public class IdCardValidatorTest extends AbstractSpringContextTest {
	// 18位身份证中，各个数字的生成校验码时的权值
	private final static int[]	VERIFY_CODE_WEIGHT	= { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
	// 18位身份证中最后一位校验码
	private final static char[]	VERIFY_CODE			= { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };

	protected static Logger		_logger				= LoggerFactory.getLogger(IdCardValidatorTest.class);

	private char getVerifyCode(String sNumber) {
		int sum = 0;
		int iNumberLength = sNumber.length() - 1;
		for (int i = 0; i < iNumberLength; i++) {
			sum += (sNumber.charAt(i) - '0') * VERIFY_CODE_WEIGHT[i];
		}
		return VERIFY_CODE[sum % 11];
	}

	/**
	 * 单线程测试
	 */
	@Test
	public void test01() {
		// 号码为空
		Assert.assertFalse(IdCardValidator.validate(null));
		Assert.assertFalse(IdCardValidator.validate(""));
		Assert.assertFalse(IdCardValidator.validate(" "));
		// 号码长度不对
		Assert.assertFalse(IdCardValidator.validate("111"));
		Assert.assertFalse(IdCardValidator.validate("1111111111111111111"));
		// 号码有其它字符
		Assert.assertFalse(IdCardValidator.validate("111a11111111111111"));
		Assert.assertFalse(IdCardValidator.validate("11111111111111111d"));
		Assert.assertFalse(IdCardValidator.validate("11111111111111111X"));
		Assert.assertFalse(IdCardValidator.validate("11111111111111111x"));
		// 日���格式不对
		Assert.assertFalse(IdCardValidator.validate("45010420150229101x"));
		Assert.assertFalse(IdCardValidator.validate("45010420140432101x"));
		// 日期超出范围
		Assert.assertFalse(IdCardValidator.validate("45010418991225101x"));
		LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
		String sDate = dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String sNumber = "450106" + sDate + "052";
		sNumber += getVerifyCode(sNumber);
		Assert.assertFalse(IdCardValidator.validate(sNumber));
		// 校验和不对
		Assert.assertFalse(IdCardValidator.validate("45010419770425101x"));
		Assert.assertFalse(IdCardValidator.validate("450106197707210526"));
		// 地址码不对
		// Assert.assertFalse(IdCardValidator.validate("950106197707210528"));
		// 正确的身份证
		Assert.assertTrue(IdCardValidator.validate("450104190001011014"));
		Assert.assertTrue(IdCardValidator.validate("450104201504011017"));
		Assert.assertTrue(IdCardValidator.validate("450104197704251016"));
		Assert.assertTrue(IdCardValidator.validate("45010619770721052X"));
		Assert.assertTrue(IdCardValidator.validate("45010619770721052x"));
	}// test01

	/**
	 * 多线程测试10万次test01
	 */
	// @Test
	public void test02() {
		ExecutorService executorService = new ThreadPoolExecutor(200, 200, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new AbortPolicy());
		final int iTaskCount = 100000;
		for (int i = 0; i < iTaskCount; i++) {
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					test01();
				}
			});
		}
	}

	@Test
	public void test03() throws IOException {
		// IdCardHelper.verify("132201198704126619");
		testFile("idcard4.txt");
	}

	private void testFile(String sFileName) throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(sFileName).getFile());
		String sIdCardNo;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			while ((sIdCardNo = reader.readLine()) != null) {
				IdCardValidator.validate(sIdCardNo);
			}
		}
	}

}
