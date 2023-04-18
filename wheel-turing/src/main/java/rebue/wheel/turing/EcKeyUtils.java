package rebue.wheel.turing;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

@Slf4j
public class EcKeyUtils {
    static {
        // 添加BouncyCastle实现
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 椭圆曲线的算法
     */
    @Getter
    public enum EcAlgorithm {
        SM2("sm2p256v1");

        private final String code;

        EcAlgorithm(String code) {
            this.code = code;
        }
    }

    public static KeyPair generateKeyPair() {
        return generateKeyPair(EcAlgorithm.SM2);
    }

    @SneakyThrows
    public static KeyPair generateKeyPair(EcAlgorithm algorithm) {
        KeyPairGenerator   keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
        ECGenParameterSpec ecParameterSpec  = new ECGenParameterSpec(algorithm.getCode());
        keyPairGenerator.initialize(ecParameterSpec);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 从密钥对中获取私钥
     *
     * @param keyPair 密钥对
     * @return 私钥
     */
    public static byte[] getPrivateKey(final KeyPair keyPair) {
        final BCECPrivateKey bcecPrivateKey = (BCECPrivateKey) keyPair.getPrivate();
        // XXX 这里压缩了私钥，只取了D
        return bcecPrivateKey.getD().toByteArray();
    }

    /**
     * 从密钥对中获取私钥，并用16进制编码生成字节数组
     *
     * @param keyPair 密钥对
     * @return 编码后的字符串
     */
    public static byte[] getPrivateKeyToHex(final KeyPair keyPair) {
        return Hex.encode(getPrivateKey(keyPair));
    }

    /**
     * 从密钥对中获取私钥，并用16进制编码生成字符串
     *
     * @param keyPair 密钥对
     * @return 编码后的字符串
     */
    public static String getPrivateKeyToHexString(final KeyPair keyPair) {
        return Hex.toHexString(getPrivateKey(keyPair));
    }

    /**
     * 从密钥对中获取私钥，并用Base64编码生成字符串
     *
     * @param keyPair 密钥对
     * @return 编码后的字符串
     */
    public static String getPrivateKeyToBase64String(final KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(getPrivateKey(keyPair));
    }

    /**
     * 从密钥对中获取私钥，并编码生成字符串
     * 先进行16进制编码，再进行BASE64编码
     *
     * @param keyPair 密钥对
     * @return 编码后的字符串
     */
    private static String getPrivateKeyToHexBase64String(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(getPrivateKeyToHex(keyPair));
    }

    /**
     * 从密钥对中获取私钥，并编码生成字符串
     * 先进行16进制编码，再进行BASE64编码
     *
     * @param keyPair 密钥对
     * @return 编码后的字符串
     */
    private static String getPrivateKeyToPem(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(getPrivateKeyToHex(keyPair));
    }

    /**
     * 从密钥对中获取私钥，并编码生成字符串
     * 先进行16进制编码，再进行BASE64编码
     *
     * @param keyPair 密钥对
     * @return 编码后的字符串
     */
    public static String getPrivateKeyToString(final KeyPair keyPair) {
        return getPrivateKeyToHexBase64String(keyPair);
    }

    /**
     * 从密钥对中获取私钥，并编码生成字符串
     *
     * @param keyPair 密钥对
     * @return 编码后的字符串
     */
    public static String getPrivateKeyToString(final KeyPair keyPair, EncodeMode encodeMode) {
        switch (encodeMode) {
            case HEX:
                return getPrivateKeyToHexString(keyPair);
            case BASE64:
                return getPrivateKeyToBase64String(keyPair);
            case HEX_BASE64:
                return getPrivateKeyToHexBase64String(keyPair);
        }
        throw new RuntimeException("unsupported encode mode");
    }

    /**
     * 从字符串中生成私钥(默认SM2算法的私钥)
     *
     * @param privateKeyEncode 私钥编码字符串(自动适配Hex或Base64编码，及先Hex再Base64编码的字符串)
     * @return 私钥
     */
    public static BCECPrivateKey getPrivateKeyFromString(final String privateKeyEncode) {
        return getPrivateKeyFromString(privateKeyEncode, EcAlgorithm.SM2);
    }

    /**
     * 从字符串中生成私钥
     *
     * @param privateKeyEncode 私钥编码字符串(自动适配Hex或Base64编码，及先Hex再Base64编码的字符串)
     * @param ecAlgorithm      椭圆曲线算法
     * @return 私钥
     */
    public static BCECPrivateKey getPrivateKeyFromString(final String privateKeyEncode, EcAlgorithm ecAlgorithm) {
        byte[]           privateKey       = AutoDecoder.decode(privateKeyEncode);
        X9ECParameters   x9ECParameters   = GMNamedCurves.getByName(ecAlgorithm.getCode());
        ECParameterSpec  ecParameterSpec  = newEcParameterSpec(x9ECParameters);
        ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(new BigInteger(1, privateKey), ecParameterSpec);
        return new BCECPrivateKey("EC", ecPrivateKeySpec, BouncyCastleProvider.CONFIGURATION);
    }

    /**
     * 从密钥对中获取公钥
     *
     * @param keyPair    密钥对
     * @param compressed 是否压缩
     * @return 公钥
     */
    public static byte[] getPublicKey(final KeyPair keyPair, boolean compressed) {
        final BCECPublicKey bcecPublicKey = (BCECPublicKey) keyPair.getPublic();
        // XXX 这里压缩了公钥，只取了Q并进行了压缩
        return bcecPublicKey.getQ().getEncoded(compressed);
    }

    /**
     * 从密钥对中获取公钥，并用16进制编码生成字节数组
     *
     * @param keyPair 密钥对
     * @return 编码后的字符串
     */
    public static byte[] getPublicKeyToHex(final KeyPair keyPair) {
        return getPublicKeyToHex(keyPair, true);
    }

    /**
     * 从密钥对中获取公钥，并用16进制编码生成字节数组
     *
     * @param keyPair    密钥对
     * @param compressed 是否压缩
     * @return 编码后的字符串
     */
    public static byte[] getPublicKeyToHex(final KeyPair keyPair, boolean compressed) {
        return Hex.encode(getPublicKey(keyPair, compressed));
    }

    /**
     * 从密钥对中获取公钥，并用16进制编码生成字符串
     *
     * @param keyPair 密钥对
     * @return 编码后的字符串
     */
    public static String getPublicKeyToHexString(final KeyPair keyPair) {
        return getPublicKeyToHexString(keyPair, true);
    }

    /**
     * 从密钥对中获取公钥，并用16进制编码生成字符串
     *
     * @param keyPair    密钥对
     * @param compressed 是否压缩
     * @return 编码后的字符串
     */
    public static String getPublicKeyToHexString(final KeyPair keyPair, boolean compressed) {
        return Hex.toHexString(getPublicKey(keyPair, compressed));
    }

    /**
     * 从密钥对中获取公钥，并用Base64编码生成字符串
     *
     * @param keyPair 密钥对
     * @return 编码后的字符串
     */
    public static String getPublicKeyToBase64String(final KeyPair keyPair) {
        return getPublicKeyToBase64String(keyPair, true);
    }

    /**
     * 从密钥对中获取公钥，并用Base64编码生成字符串
     *
     * @param keyPair    密钥对
     * @param compressed 是否压缩
     * @return 编码后的字符串
     */
    public static String getPublicKeyToBase64String(final KeyPair keyPair, boolean compressed) {
        return Base64.getEncoder().encodeToString(getPublicKey(keyPair, compressed));
    }

    /**
     * 从密钥对中获取公钥，并编码生成字符串
     * 先进行16进制编码，再进行BASE64编码
     *
     * @param keyPair 密钥对
     * @return 编码后的字符串
     */
    private static String getPublicKeyToHexBaseString(KeyPair keyPair) {
        return getPublicKeyToHexBaseString(keyPair, true);
    }

    /**
     * 从密钥对中获取公钥，并编码生成字符串
     * 先进行16进制编码，再进行BASE64编码
     *
     * @param keyPair    密钥对
     * @param compressed 是否压缩
     * @return 编码后的字符串
     */
    private static String getPublicKeyToHexBaseString(KeyPair keyPair, boolean compressed) {
        return Base64.getEncoder().encodeToString(getPublicKeyToHex(keyPair, compressed));
    }

    /**
     * 从密钥对中获取公钥，并编码生成字符串
     * 先进行16进制编码，再进行BASE64编码
     *
     * @param keyPair 密钥对
     * @return 编码后的字符串
     */
    public static String getPublicKeyToString(final KeyPair keyPair) {
        return getPublicKeyToString(keyPair, true);
    }

    /**
     * 从密钥对中获取公钥，并编码生成字符串
     * 先进行16进制编码，再进行BASE64编码
     *
     * @param keyPair    密钥对
     * @param compressed 是否压缩
     * @return 编码后的字符串
     */
    public static String getPublicKeyToString(final KeyPair keyPair, boolean compressed) {
        return getPublicKeyToHexBaseString(keyPair, compressed);
    }

    /**
     * 从密钥对中获取公钥，并编码生成字符串
     *
     * @param keyPair    密钥对
     * @param encodeMode 编码方式
     * @param compressed 是否压缩
     * @return 编码后的字符串
     */
    public static String getPublicKeyToString(final KeyPair keyPair, EncodeMode encodeMode, boolean compressed) {
        switch (encodeMode) {
            case HEX:
                return getPublicKeyToHexString(keyPair, compressed);
            case BASE64:
                return getPublicKeyToBase64String(keyPair, compressed);
            case HEX_BASE64:
                return getPublicKeyToHexBaseString(keyPair, compressed);
        }
        throw new RuntimeException("unsupported encode mode");
    }

    /**
     * 从字符串中生成公钥(默认SM2算法的公钥)
     *
     * @param publicKeyEncode 公钥编码字符串(自动适配Hex或Base64编码，及先Hex再Base64编码的字符串)
     * @return 公钥
     */
    public static BCECPublicKey getPublicKeyFromString(final String publicKeyEncode) {
        return getPublicKeyFromString(publicKeyEncode, EcAlgorithm.SM2);
    }

    /**
     * 从字符串中生成公钥
     *
     * @param publicKeyEncode 公钥编码字符串(自动适配Hex或Base64编码，及先Hex再Base64编码的字符串)
     * @param ecAlgorithm     椭圆曲线的算法
     * @return 公钥
     */
    public static BCECPublicKey getPublicKeyFromString(final String publicKeyEncode, EcAlgorithm ecAlgorithm) {
        byte[]                publicKey       = AutoDecoder.decode(publicKeyEncode);
        X9ECParameters        x9ECParameters  = GMNamedCurves.getByName(ecAlgorithm.getCode());
        ECParameterSpec       ecParameterSpec = newEcParameterSpec(x9ECParameters);
        final ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(x9ECParameters.getCurve().decodePoint(publicKey), ecParameterSpec);
        return new BCECPublicKey("EC", ecPublicKeySpec, BouncyCastleProvider.CONFIGURATION);
    }

    /**
     * 创建新的椭圆曲线参数
     *
     * @param x9ECParameters 椭圆曲线参数
     * @return 椭圆曲线参数
     */
    private static ECParameterSpec newEcParameterSpec(X9ECParameters x9ECParameters) {
        return new ECParameterSpec(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH(), x9ECParameters.getSeed());
    }


}
