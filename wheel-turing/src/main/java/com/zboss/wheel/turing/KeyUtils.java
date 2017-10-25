package com.zboss.wheel.turing;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyUtils {
	/**
	 * 通过DER格式的公钥得到PublicKey对象
	 */
	public static PublicKey getPublicKeyByDer(String algorithm, byte[] publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchProviderException {
		// 获取公钥
		KeySpec keySpec = new X509EncodedKeySpec(publicKey);
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm, "BC");
		return keyFactory.generatePublic(keySpec);
	}

	/**
	 * 通过PEM格式的私钥得到PrivateKey对象
	 *
	 * @throws java.io.IOException
	 */
	public static PublicKey getPublicKeyByPem(byte[] pem) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IOException {
		Reader reader = new InputStreamReader(new ByteArrayInputStream(pem));
		try (PEMParser pemReader = new PEMParser(reader)) {
			SubjectPublicKeyInfo subjectPublicKeyInfo = (SubjectPublicKeyInfo) pemReader.readObject();
			return new JcaPEMKeyConverter().setProvider("BC").getPublicKey(subjectPublicKeyInfo);
		}
	}

	/**
	 * 通过DER格式的私钥得到PrivateKey对象
	 */
	public static PrivateKey getPrivateKeyByDer(String algorithm, byte[] privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchProviderException {
		// 获取私钥
		KeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm, "BC");
		return keyFactory.generatePrivate(keySpec);
	}

	/**
	 * 通过PEM格式的私钥得到PrivateKey对象
	 *
	 * @throws java.io.IOException
	 */
	public static PrivateKey getPrivateKeyByPem(byte[] pem) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IOException {
		Reader reader = new InputStreamReader(new ByteArrayInputStream(pem));
		try (PEMParser pemReader = new PEMParser(reader)) {
			Object keyPairObject = pemReader.readObject();
			if (keyPairObject instanceof PEMEncryptedKeyPair) {
				keyPairObject = ((PEMEncryptedKeyPair) keyPairObject).decryptKeyPair(new JcePEMDecryptorProviderBuilder().build("xxxxxxxx".toCharArray()));
			}
			PEMKeyPair pemKeyPair = (PEMKeyPair) keyPairObject;
			KeyPair keyPair = new JcaPEMKeyConverter().setProvider("BC").getKeyPair(pemKeyPair);
			return keyPair.getPrivate();
		}
	}
}
