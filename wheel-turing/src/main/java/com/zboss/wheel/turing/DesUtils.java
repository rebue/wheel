package com.zboss.wheel.turing;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class DesUtils {
	public static final String	KEY_ALGORITHM		= "DES";
	// public static final String CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";
//    public static final String CIPHER_ALGORITHM = "DES/ECB/PKCS7Padding";
	public static final String	CIPHER_ALGORITHM	= "DES/ECB/NoPadding";

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	private static byte get7Bits(byte[] input, int startBit) {
		int word;
		word = input[startBit / 8] << 8;
		word |= input[startBit / 8 + 1] & 0xff;
		word >>= 15 - (startBit % 8 + 7);
		return (byte) (word & 0xFE);
	}

	private static byte[] makeKey(byte[] key) {
		byte[] des_key = new byte[8];
		byte[] padding_key = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		System.arraycopy(key, 0, padding_key, 0, key.length);
		des_key[0] = get7Bits(padding_key, 0);
		des_key[1] = get7Bits(padding_key, 7);
		des_key[2] = get7Bits(padding_key, 14);
		des_key[3] = get7Bits(padding_key, 21);
		des_key[4] = get7Bits(padding_key, 28);
		des_key[5] = get7Bits(padding_key, 35);
		des_key[6] = get7Bits(padding_key, 42);
		des_key[7] = get7Bits(padding_key, 49);
		return des_key;
	}

	public static byte[] encrypt(byte[] key, byte[] toEncrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");

		if (key.length == 7)
			key = makeKey(key);
		else if (key.length != 8)
			throw new InvalidKeyException("key length must is 7 or 8 bytes");

		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, KEY_ALGORITHM));
		return cipher.doFinal(toEncrypt);
	}

	public static byte[] decrypt(byte[] key, byte[] encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, KEY_ALGORITHM));
		return cipher.doFinal(encrypted);
	}

}
