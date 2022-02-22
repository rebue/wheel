package rebue.wheel.turing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.util.encoders.Hex;

/**
 * KeyPair的工具类
 * 
 * @author zbz
 *
 */
public class KeyPairUtils {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
    }

    public static String getPublicKeyString(final KeyPair keyPair) {
        final BCECPublicKey bcecPublicKey      = (BCECPublicKey) keyPair.getPublic();
        final String        publicKeyStr       = Hex.toHexString(bcecPublicKey.getQ().getEncoded(false));
        final String        publicKeyStrBase64 = new String(Base64.getEncoder().encode(publicKeyStr.getBytes(DEFAULT_CHARSET)), DEFAULT_CHARSET);
        return publicKeyStrBase64;
    }

    public static String getPrivateKeyString(final KeyPair keyPair) {
        final BCECPrivateKey bcecPrivateKey   = (BCECPrivateKey) keyPair.getPrivate();
        final String         privateKeyStr    = Hex.toHexString(bcecPrivateKey.getD().toByteArray());
        final String         privateKeyBase64 = new String(Base64.getEncoder().encode(privateKeyStr.getBytes(DEFAULT_CHARSET)), DEFAULT_CHARSET);
        return privateKeyBase64;
    }

    /**
     * 通过DER格式的公钥得到PublicKey对象
     */
    public static PublicKey getPublicKeyByDer(final String algorithm, final byte[] publicKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchProviderException {
        // 获取公钥
        final KeySpec    keySpec    = new X509EncodedKeySpec(publicKey);
        final KeyFactory keyFactory = KeyFactory.getInstance(algorithm, "BC");
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 通过PEM格式的私钥得到PrivateKey对象
     *
     * @throws java.io.IOException
     */
    public static PublicKey getPublicKeyByPem(final byte[] pem) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IOException {
        final Reader reader = new InputStreamReader(new ByteArrayInputStream(pem));
        try (PEMParser pemReader = new PEMParser(reader)) {
            final SubjectPublicKeyInfo subjectPublicKeyInfo = (SubjectPublicKeyInfo) pemReader.readObject();
            return new JcaPEMKeyConverter().setProvider("BC").getPublicKey(subjectPublicKeyInfo);
        }
    }

    /**
     * 通过DER格式的私钥得到PrivateKey对象
     */
    public static PrivateKey getPrivateKeyByDer(final String algorithm, final byte[] privateKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchProviderException {
        // 获取私钥
        final KeySpec    keySpec    = new PKCS8EncodedKeySpec(privateKey);
        final KeyFactory keyFactory = KeyFactory.getInstance(algorithm, "BC");
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 通过PEM格式的私钥得到PrivateKey对象
     *
     * @throws java.io.IOException
     */
    public static PrivateKey getPrivateKeyByPem(final byte[] pem) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IOException {
        final Reader reader = new InputStreamReader(new ByteArrayInputStream(pem));
        try (PEMParser pemReader = new PEMParser(reader)) {
            Object keyPairObject = pemReader.readObject();
            if (keyPairObject instanceof PEMEncryptedKeyPair) {
                keyPairObject = ((PEMEncryptedKeyPair) keyPairObject).decryptKeyPair(new JcePEMDecryptorProviderBuilder().build("xxxxxxxx".toCharArray()));
            }
            final PEMKeyPair pemKeyPair = (PEMKeyPair) keyPairObject;
            final KeyPair    keyPair    = new JcaPEMKeyConverter().setProvider("BC").getKeyPair(pemKeyPair);
            return keyPair.getPrivate();
        }
    }
}
