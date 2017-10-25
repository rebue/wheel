package com.zboss.wheel.turing;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;

public class RsaUtils {
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static final String	KEY_ALGORITHM		= "RSA";
	public static final String	ENCRYPT_ALGORITHM	= "RSA/ECB/PKCS1Padding";

	/**
	 * 生成KeyPair
	 */
	public static KeyPair genKeyPair() throws NoSuchProviderException, NoSuchAlgorithmException {
		return genKeyPair(2048);
	}

	/**
	 * /** 生成KeyPair
	 */
	public static KeyPair genKeyPair(int keySize) throws NoSuchProviderException, NoSuchAlgorithmException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RsaUtils.KEY_ALGORITHM, "BC");
		keyPairGenerator.initialize(keySize);
		return keyPairGenerator.generateKeyPair();
	}

	/**
	 * 用公钥加密
	 */
	public static byte[] encrypt(Key key, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
		Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM, "BC");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(data);
	}

	/**
	 * 用私钥解密
	 */
	public static byte[] decrypt(Key key, byte[] encryptData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
		Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM, "BC");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(encryptData);
	}

	public static byte[] getPubKeyDataForC(byte[] bPubKey) throws UnsupportedEncodingException {
		byte[] pub = new byte[bPubKey.length - 24];
		System.arraycopy(bPubKey, 24, pub, 0, bPubKey.length - 24);
		return pub;
	}

}
