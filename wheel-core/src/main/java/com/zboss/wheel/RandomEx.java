package com.zboss.wheel;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * @since 1.7
 */
public class RandomEx {
	private static String factor1 = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static String factor2 = "1234567890";
	private static SecureRandom random;

	static {
		try {
			random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
		}
	}

	public static Random getRandom() {
		return random;
	}

	/**
	 * 随机生成UUID（不含破折号）
	 */
	public static String randomUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
	}

	/**
	 * 生成resultSize位的随机数(只包含数字和大小写的字母)
	 */
	public static String random1(int resultSize) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < resultSize; i++) {
			stringBuilder.append(factor1.charAt(random.nextInt(factor1.length())));
		}
		return stringBuilder.toString();
	}

	/**
	 * 生成resultSize位的随机数(只包含数字)
	 */
	public static String random2(int resultSize) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < resultSize; i++) {
			stringBuilder.append(factor2.charAt(random.nextInt(factor2.length())));
		}
		return stringBuilder.toString();
	}
}
