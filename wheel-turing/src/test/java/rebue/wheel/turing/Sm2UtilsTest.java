package rebue.wheel.turing;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class Sm2UtilsTest {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    // 固定值，SM2签名的标识
    private static final String USER_ID = "1234567812345678";

    /**
     * 测试生成公私钥对
     */
    @Test
    public void test01_genKeyPair() {
        log.info("生成公私钥对");
        KeyPair keyPair          = BcEcKeyUtils.generateKeyPair();
        String  privateKeyString = BcEcKeyUtils.getPrivateKeyToString(keyPair);
        log.info("生成Hex_Base64密钥");
        log.info("  私钥: {}", privateKeyString);
        String uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToString(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        String compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToString(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Hex_Base64私钥");
        BCECPrivateKey privateKey = BcEcKeyUtils.getPrivateKeyFromString(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Hex_Base64公钥(非压缩)");
        BCECPublicKey uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromString(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        log.info("获取Hex_Base64公钥(压缩)");
        BCECPublicKey compressedPublicKey = BcEcKeyUtils.getPublicKeyFromString(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);

        log.info("生成Hex密钥");
        privateKeyString = BcEcKeyUtils.getPrivateKeyToHexString(keyPair);
        log.info("  私钥: {}", privateKeyString);
        uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToHexString(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToHexString(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Hex私钥");
        privateKey = BcEcKeyUtils.getPrivateKeyFromString(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Hex公钥(非压缩)");
        uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromString(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        log.info("获取Hex公钥(压缩)");
        compressedPublicKey = BcEcKeyUtils.getPublicKeyFromString(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);

        log.info("生成Base64密钥");
        privateKeyString = BcEcKeyUtils.getPrivateKeyToBase64String(keyPair);
        log.info("私钥: {}", privateKeyString);
        uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToBase64String(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToBase64String(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Base64私钥");
        privateKey = BcEcKeyUtils.getPrivateKeyFromString(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Base64公钥(非压缩)");
        uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromString(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        Assertions.assertNotNull(privateKey);
        log.info("获取Base64公钥(压缩)");
        compressedPublicKey = BcEcKeyUtils.getPublicKeyFromString(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);
    }


    /**
     * 加密与解密
     */
    @Test
    public void test02_encrypt() throws Exception {
        log.info("生成公私钥对");
        KeyPair keyPair          = BcEcKeyUtils.generateKeyPair();
        String  privateKeyString = BcEcKeyUtils.getPrivateKeyToString(keyPair);
        log.info("生成Hex_Base64密钥");
        log.info("  私钥: {}", privateKeyString);
        String uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToString(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        String compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToString(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Hex_Base64私钥");
        BCECPrivateKey privateKey = BcEcKeyUtils.getPrivateKeyFromString(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Hex_Base64公钥(非压缩)");
        BCECPublicKey uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromString(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        log.info("获取Hex_Base64公钥(压缩)");
        BCECPublicKey compressedPublicKey = BcEcKeyUtils.getPublicKeyFromString(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);

        log.info("生成Hex密钥");
        privateKeyString = BcEcKeyUtils.getPrivateKeyToHexString(keyPair);
        log.info("  私钥: {}", privateKeyString);
        uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToHexString(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToHexString(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Hex私钥");
        privateKey = BcEcKeyUtils.getPrivateKeyFromString(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Hex公钥(非压缩)");
        uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromString(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        log.info("获取Hex公钥(压缩)");
        compressedPublicKey = BcEcKeyUtils.getPublicKeyFromString(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);

        log.info("生成Base64密钥");
        privateKeyString = BcEcKeyUtils.getPrivateKeyToBase64String(keyPair);
        log.info("私钥: {}", privateKeyString);
        uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToBase64String(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToBase64String(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Base64私钥");
        privateKey = BcEcKeyUtils.getPrivateKeyFromString(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Base64公钥(非压缩)");
        uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromString(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        Assertions.assertNotNull(privateKey);
        log.info("获取Base64公钥(压缩)");
        compressedPublicKey = BcEcKeyUtils.getPublicKeyFromString(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);

        String plainText = "你好，World！Hello, 世界!";
        log.info("加密: {}", plainText);

        String encryptedData = Sm2Utils.encrypt(plainText, uncompressedPublicKey);
        log.info("公钥(非压缩)加密后的数据: {}", encryptedData);
        String decryptText = Sm2Utils.decrypt(encryptedData, privateKey);
        log.info("解密后的文本: {}", decryptText);

        encryptedData = Sm2Utils.encrypt(plainText, compressedPublicKey);
        log.info("公钥(压缩)加密后的数据: {}", encryptedData);
        decryptText = Sm2Utils.decrypt(encryptedData, privateKey);
        log.info("解密后的文本: {}", decryptText);
    }

    /**
     * 解密在线网站加密的数据
     * https://the-x.cn/cryptography/Sm2.aspx
     */
    @Test
    public void test03_decrypt_online() throws InvalidCipherTextException {
        String privateKeyString = "46c90cfd6babaef118fdf23c0748675dde9f7bfb0221a88c300d1f2f60241740";
        log.info("获取私钥");
        BCECPrivateKey privateKey = BcEcKeyUtils.getPrivateKeyFromString(privateKeyString);
        Assertions.assertNotNull(privateKey);
        String encryptedData = "BF7Fr9bh/Gn5pBr1+N7MNQuuMr9RLXCxDhFExGt9AT/WtbcunONh8nay/u7raZ5XspZGurZTUX728JYTXgQ7v+IH0w0UD8wbcKVPricfQgEjwgGk6VD5JnxVr0z7J5LOfyTofuoeJlbgP4mslpZSUS/Qdh3+xOC3ncyWioX+IJk=";
        log.info("在线网页加密后的数据: {}", encryptedData);
        String decryptText = Sm2Utils.decrypt(encryptedData, privateKey);
        log.info("解密后的文本: {}", decryptText);
        Assertions.assertEquals("你好，World！Hello, 世界!", decryptText);
    }

    /**
     * 签名与验签
     */
    @Test
    public void test04_sign() {
        log.info("生成公私钥对");
        KeyPair keyPair          = BcEcKeyUtils.generateKeyPair();
        String  privateKeyString = BcEcKeyUtils.getPrivateKeyToString(keyPair);
        log.info("生成Hex_Base64密钥");
        log.info("  私钥: {}", privateKeyString);
        String uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToString(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        String compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToString(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Hex_Base64私钥");
        BCECPrivateKey privateKey = BcEcKeyUtils.getPrivateKeyFromString(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Hex_Base64公钥(非压缩)");
        BCECPublicKey uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromString(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        log.info("获取Hex_Base64公钥(压缩)");
        BCECPublicKey compressedPublicKey = BcEcKeyUtils.getPublicKeyFromString(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);

        log.info("生成Hex密钥");
        privateKeyString = BcEcKeyUtils.getPrivateKeyToHexString(keyPair);
        log.info("  私钥: {}", privateKeyString);
        uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToHexString(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToHexString(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Hex私钥");
        privateKey = BcEcKeyUtils.getPrivateKeyFromString(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Hex公钥(非压缩)");
        uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromString(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        log.info("获取Hex公钥(压缩)");
        compressedPublicKey = BcEcKeyUtils.getPublicKeyFromString(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);

        log.info("生成Base64密钥");
        privateKeyString = BcEcKeyUtils.getPrivateKeyToBase64String(keyPair);
        log.info("私钥: {}", privateKeyString);
        uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToBase64String(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToBase64String(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Base64私钥");
        privateKey = BcEcKeyUtils.getPrivateKeyFromString(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Base64公钥(非压缩)");
        uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromString(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        Assertions.assertNotNull(privateKey);
        log.info("获取Base64公钥(压缩)");
        compressedPublicKey = BcEcKeyUtils.getPublicKeyFromString(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);

        String plainText = "你好，World！Hello, 世界!";
        log.info("要签名的文本: {}", plainText);

        final String sign = sign(privateKey, plainText);
        log.info("生成签名： {}", sign);

        boolean verifySignResult = verifySign(uncompressedPublicKey, plainText, sign);
        log.info("公钥(非压缩)验签结果： {}", verifySignResult);
        Assertions.assertTrue(verifySignResult);
        verifySignResult = verifySign(compressedPublicKey, plainText, sign);
        log.info("公钥(压缩)验签结果： {}", verifySignResult);
        Assertions.assertTrue(verifySignResult);
    }

    /**
     * 私钥签名
     *
     * @param privateKey 私钥
     * @param plainText  签名参数
     */
    public static String sign(final PrivateKey privateKey, final String plainText) {
        final byte[] userId = USER_ID.getBytes(DEFAULT_CHARSET);
        final byte[] msg    = plainText.getBytes(DEFAULT_CHARSET);
        final byte[] signed = Sm2Utils.signSm3WithSm2(msg, userId, privateKey);
        return Base64.getEncoder().encodeToString(signed);
    }

    /**
     * 公钥验签
     *
     * @param publicKey 公钥
     * @param plainText 签名参数
     * @param sign      签名
     */
    public static boolean verifySign(final PublicKey publicKey, final String plainText, final String sign) {
        try {
            final byte[] signed = Base64.getDecoder().decode(sign.getBytes());
            final byte[] msg    = plainText.getBytes(DEFAULT_CHARSET);
            final byte[] userId = USER_ID.getBytes(DEFAULT_CHARSET);
            return Sm2Utils.verifySm3WithSm2(msg, userId, signed, publicKey);
        } catch (final Exception e) {
            log.error("验签出现异常", e);
            return false;
        }
    }

}
