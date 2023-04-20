package rebue.wheel.turing;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 通用密钥工具类
 */
public class KeyUtils {
    static {
        // 添加BouncyCastle实现
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 从编码的私钥字符串获取私钥对象
     *
     * @param privateKeyEncode 编码的私钥字符串
     * @param algorithm        生成密钥的算法
     * @return 私钥
     * @throws NoSuchAlgorithmException 算法不支持
     * @throws InvalidKeySpecException  私钥字符串不正确
     */
    public static PrivateKey getPrivateKeyFromString(String privateKeyEncode, String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance(algorithm, "BC");
        } catch (NoSuchProviderException e) {
            throw new RuntimeException("没有导入BC库");
        }
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(AutoDecoder.decode(privateKeyEncode));
        return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }

    /**
     * 从编码的公钥字符串获取公钥对象
     *
     * @param publicKeyEncode 编码的公钥字符串
     * @param algorithm       生成密钥的算法
     * @return 公钥
     * @throws NoSuchAlgorithmException 算法不支持
     * @throws InvalidKeySpecException  公钥字符串不正确
     */
    public static PublicKey getPublicKeyFromString(String publicKeyEncode, String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance(algorithm, "BC");
        } catch (NoSuchProviderException e) {
            throw new RuntimeException("没有导入BC库");
        }
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(AutoDecoder.decode(publicKeyEncode));
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    /**
     * 对密钥进行Base64编码
     *
     * @param key 密钥
     * @return 编码后的字符串
     */
    public static String encodeBase64ToString(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * 对密钥进行16进制编码
     *
     * @param key 密钥
     * @return 编码后的字节数据
     */
    public static byte[] encodeHex(Key key) {
        return Hex.encode(key.getEncoded());
    }

    /**
     * 对密钥进行16进制编码
     *
     * @param key 密钥
     * @return 编码后的字符串
     */
    public static String encodeHexToString(Key key) {
        return Hex.toHexString(key.getEncoded());
    }

    /**
     * 对密钥进行16进制编码，再进行Base64编码
     *
     * @param key 密钥
     * @return 编码后的字符串
     */
    public static String encodeHexBase64ToString(Key key) {
        return Base64.getEncoder().encodeToString(encodeHex(key));
    }

    /**
     * 对密钥进行编码
     *
     * @param key 密钥
     * @return 编码后的字符串
     */
    public static String encode(Key key) {
        return encodeBase64ToString(key);
    }

    /**
     * 对密钥进行编码
     *
     * @param key 密钥
     * @return 编码后的字符串
     */
    public static String encode(Key key, EncodeMode encodeMode) {
        switch (encodeMode) {
            case HEX:
                return encodeHexToString(key);
            case BASE64:
                return encodeBase64ToString(key);
            case HEX_BASE64:
                return encodeHexBase64ToString(key);
        }
        throw new RuntimeException();   // 代码不会运行到这里
    }


}
