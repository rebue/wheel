package rebue.wheel.turing;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public class EcKeyUtils extends KeyUtils {
    /**
     * 从编码的私钥字符串获取私钥对象
     *
     * @param privateKeyEncode 编码的私钥字符串
     * @return 私钥
     * @throws NoSuchAlgorithmException 算法不支持
     * @throws InvalidKeySpecException  私钥字符串不正确
     */
    public static PrivateKey getPrivateKeyFromString(String privateKeyEncode) throws InvalidKeySpecException {
        try {
            return getPrivateKeyFromString(privateKeyEncode, "EC");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("代码不应该会运行到这里");
        }
    }

    /**
     * 从编码的公钥字符串获取公钥对象
     *
     * @param publicKeyEncode 编码的公钥字符串
     * @return 公钥
     * @throws NoSuchAlgorithmException 算法不支持
     * @throws InvalidKeySpecException  公钥字符串不正确
     */
    public static PublicKey getPublicKeyFromString(String publicKeyEncode) throws InvalidKeySpecException {
        try {
            return getPublicKeyFromString(publicKeyEncode, "EC");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("代码不应该会运行到这里");
        }
    }
}
