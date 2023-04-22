package rebue.wheel.turing;

import com.github.f4b6a3.ulid.UlidCreator;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class Sm2UtilsTest {
    // 固定值，SM2签名的标识
    private static final String USER_ID = "1234567812345678";

    /**
     * 测试生成公私钥对
     */
    @Test
    public void test01_genKeyPair() {
        log.info("生成公私钥对");
        KeyPair keyPair          = BcEcKeyUtils.generateKeyPair();
        String  privateKeyString = BcEcKeyUtils.getPrivateKeyToStr(keyPair);
        log.info("生成Hex_Base64密钥");
        log.info("  私钥: {}", privateKeyString);
        String uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToStr(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        String compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToStr(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Hex_Base64私钥");
        BCECPrivateKey privateKey = BcEcKeyUtils.getPrivateKeyFromStr(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Hex_Base64公钥(非压缩)");
        BCECPublicKey uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        log.info("获取Hex_Base64公钥(压缩)");
        BCECPublicKey compressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);

        log.info("生成Hex密钥");
        privateKeyString = BcEcKeyUtils.getPrivateKeyToHexStr(keyPair);
        log.info("  私钥: {}", privateKeyString);
        uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToHexStr(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToHexStr(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Hex私钥");
        privateKey = BcEcKeyUtils.getPrivateKeyFromStr(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Hex公钥(非压缩)");
        uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        log.info("获取Hex公钥(压缩)");
        compressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);

        log.info("生成Base64密钥");
        privateKeyString = BcEcKeyUtils.getPrivateKeyToBase64Str(keyPair);
        log.info("私钥: {}", privateKeyString);
        uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToBase64Str(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToBase64Str(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Base64私钥");
        privateKey = BcEcKeyUtils.getPrivateKeyFromStr(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Base64公钥(非压缩)");
        uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        Assertions.assertNotNull(privateKey);
        log.info("获取Base64公钥(压缩)");
        compressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);
    }


    /**
     * 加密与解密
     */
    @RepeatedTest(100)
    @Execution(ExecutionMode.CONCURRENT)
    public void test02_encrypt() throws Exception {
        log.info("生成公私钥对");
        KeyPair keyPair          = BcEcKeyUtils.generateKeyPair();
        String  privateKeyString = BcEcKeyUtils.getPrivateKeyToStr(keyPair);
        log.info("生成Hex_Base64密钥");
        log.info("  私钥: {}", privateKeyString);
        String uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToStr(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        String compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToStr(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Hex_Base64私钥");
        BCECPrivateKey privateKey = BcEcKeyUtils.getPrivateKeyFromStr(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Hex_Base64公钥(非压缩)");
        BCECPublicKey uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        log.info("获取Hex_Base64公钥(压缩)");
        BCECPublicKey compressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);

        log.info("生成Hex密钥");
        privateKeyString = BcEcKeyUtils.getPrivateKeyToHexStr(keyPair);
        log.info("  私钥: {}", privateKeyString);
        uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToHexStr(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToHexStr(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Hex私钥");
        privateKey = BcEcKeyUtils.getPrivateKeyFromStr(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Hex公钥(非压缩)");
        uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        log.info("获取Hex公钥(压缩)");
        compressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);

        log.info("生成Base64密钥");
        privateKeyString = BcEcKeyUtils.getPrivateKeyToBase64Str(keyPair);
        log.info("私钥: {}", privateKeyString);
        uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToBase64Str(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToBase64Str(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Base64私钥");
        privateKey = BcEcKeyUtils.getPrivateKeyFromStr(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Base64公钥(非压缩)");
        uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        Assertions.assertNotNull(privateKey);
        log.info("获取Base64公钥(压缩)");
        compressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);

        String plainText = "你好，World！Hello, 世界!" + UlidCreator.getUlid();
        log.info("加密: {}", plainText);

        String encryptedData = Sm2Utils.encrypt(plainText, uncompressedPublicKey);
        log.info("公钥(非压缩)加密后的数据: {}", encryptedData);
        String decryptText = Sm2Utils.decrypt(encryptedData, privateKey);
        log.info("解密后的文本: {}", decryptText);
        Assertions.assertEquals(plainText, decryptText);

        encryptedData = Sm2Utils.encrypt(plainText, compressedPublicKey);
        log.info("公钥(压缩)加密后的数据: {}", encryptedData);
        decryptText = Sm2Utils.decrypt(encryptedData, privateKey);
        log.info("解密后的文本: {}", decryptText);
        Assertions.assertEquals(plainText, decryptText);
    }

    /**
     * 解密在线网站加密的数据
     * <a href="https://the-x.cn/cryptography/Sm2.aspx">在线网站</a>
     */
    @Test
    public void test03_decrypt_online() throws InvalidCipherTextException {
        String privateKeyString = "46c90cfd6babaef118fdf23c0748675dde9f7bfb0221a88c300d1f2f60241740";
        log.info("获取私钥");
        PrivateKey privateKey = BcEcKeyUtils.getPrivateKeyFromStr(privateKeyString);
        Assertions.assertNotNull(privateKey);
        String encryptedData = "BF7Fr9bh/Gn5pBr1+N7MNQuuMr9RLXCxDhFExGt9AT/WtbcunONh8nay/u7raZ5XspZGurZTUX728JYTXgQ7v+IH0w0UD8wbcKVPricfQgEjwgGk6VD5JnxVr0z7J5LOfyTofuoeJlbgP4mslpZSUS/Qdh3+xOC3ncyWioX+IJk=";
        log.info("在线网页加密后的数据: {}", encryptedData);
        String decryptText = Sm2Utils.decrypt(encryptedData, privateKey);
        log.info("解密后的文本: {}", decryptText);
        Assertions.assertEquals("你好，World！Hello, 世界!", decryptText);
    }

    /**
     * 解密demo的数据
     */
    @Test
    public void test03_decrypt_demo() throws InvalidCipherTextException {
        String privateKeyString = "AI35ykfJEUGR9qko5UVztm3zuDAT82ynEooMLV5rQVYw";
        log.info("获取私钥");
        PrivateKey privateKey = BcEcKeyUtils.getPrivateKeyFromStr(privateKeyString);
        Assertions.assertNotNull(privateKey);
        String encryptedData = "BO1AkXoL2qaQkFweHLBfOzSMz4GccNuf0J4L5aX/RDG6bmic+g/HngfgUJClic93O/vDR2NkyrHFhA4kNR/AFPmH3u+lfSLmUB0Dyy3WGUsMr4s68Qo4i+uAd8Ln8hGhY0PP+BmXFKPMv5N1XBNO9lfzrzE5xy48vR8jD1LL78Q=";
        log.info("Demo加密后的数据: {}", encryptedData);
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
        String  privateKeyString = BcEcKeyUtils.getPrivateKeyToStr(keyPair);
        log.info("生成Hex_Base64密钥");
        log.info("  私钥: {}", privateKeyString);
        String uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToStr(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        String compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToStr(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Hex_Base64私钥");
        BCECPrivateKey privateKey = BcEcKeyUtils.getPrivateKeyFromStr(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Hex_Base64公钥(非压缩)");
        BCECPublicKey uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        log.info("获取Hex_Base64公钥(压缩)");
        BCECPublicKey compressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);

        log.info("生成Hex密钥");
        privateKeyString = BcEcKeyUtils.getPrivateKeyToHexStr(keyPair);
        log.info("  私钥: {}", privateKeyString);
        uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToHexStr(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToHexStr(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Hex私钥");
        privateKey = BcEcKeyUtils.getPrivateKeyFromStr(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Hex公钥(非压缩)");
        uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        log.info("获取Hex公钥(压缩)");
        compressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);

        log.info("生成Base64密钥");
        privateKeyString = BcEcKeyUtils.getPrivateKeyToBase64Str(keyPair);
        log.info("私钥: {}", privateKeyString);
        uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToBase64Str(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToBase64Str(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Base64私钥");
        privateKey = BcEcKeyUtils.getPrivateKeyFromStr(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Base64公钥(非压缩)");
        uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        Assertions.assertNotNull(privateKey);
        log.info("获取Base64公钥(压缩)");
        compressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);

        String plainText = "你好，World！Hello, 世界!";
        log.info("要签名的文本: {}", plainText);

        final String sign = sign(privateKey, plainText);
        log.info("生成签名: {}", sign);

        boolean verifySignResult = verifySign(uncompressedPublicKey, plainText, sign);
        log.info("公钥(非压缩)验签结果: {}", verifySignResult);
        Assertions.assertTrue(verifySignResult);
        verifySignResult = verifySign(compressedPublicKey, plainText, sign);
        log.info("公钥(压缩)验签结果: {}", verifySignResult);
        Assertions.assertTrue(verifySignResult);
    }

    /**
     * 私钥签名
     *
     * @param privateKey 私钥
     * @param plainText  签名参数
     */
    public static String sign(final PrivateKey privateKey, final String plainText) {
        return Sm2Utils.signSm3WithSm2(plainText, USER_ID, privateKey);
    }

    /**
     * 公钥验签
     *
     * @param publicKey 公钥
     * @param plainText 签名参数
     * @param signed    签名
     */
    public static boolean verifySign(final PublicKey publicKey, final String plainText, final String signed) {
        try {
            return Sm2Utils.verifySm3WithSm2(plainText, USER_ID, signed, publicKey);
        } catch (final Exception e) {
            log.error("验签出现异常", e);
            return false;
        }
    }

}

@Slf4j
@Nested
class Sm2ConcurrentTests {
    private static BCECPrivateKey privateKey;
    private static BCECPublicKey  uncompressedPublicKey;
    private static BCECPublicKey  compressedPublicKey;

    @BeforeAll
    static void beforeAll() {
        log.info("生成公私钥对");
        KeyPair keyPair          = BcEcKeyUtils.generateKeyPair();
        String  privateKeyString = BcEcKeyUtils.getPrivateKeyToStr(keyPair);
        log.info("生成Hex_Base64密钥");
        log.info("  私钥: {}", privateKeyString);
        String uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToStr(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        String compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToStr(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Hex_Base64私钥");
        privateKey = BcEcKeyUtils.getPrivateKeyFromStr(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Hex_Base64公钥(非压缩)");
        uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        log.info("获取Hex_Base64公钥(压缩)");
        compressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);

        log.info("生成Hex密钥");
        privateKeyString = BcEcKeyUtils.getPrivateKeyToHexStr(keyPair);
        log.info("  私钥: {}", privateKeyString);
        uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToHexStr(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToHexStr(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Hex私钥");
        privateKey = BcEcKeyUtils.getPrivateKeyFromStr(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Hex公钥(非压缩)");
        uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        log.info("获取Hex公钥(压缩)");
        compressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);

        log.info("生成Base64密钥");
        privateKeyString = BcEcKeyUtils.getPrivateKeyToBase64Str(keyPair);
        log.info("私钥: {}", privateKeyString);
        uncompressedPublicKeyString = BcEcKeyUtils.getPublicKeyToBase64Str(keyPair, false);
        log.info("  公钥(非压缩): {}", uncompressedPublicKeyString);
        compressedPublicKeyString = BcEcKeyUtils.getPublicKeyToBase64Str(keyPair);
        log.info("  公钥(压缩): {}", compressedPublicKeyString);
        log.info("获取Base64私钥");
        privateKey = BcEcKeyUtils.getPrivateKeyFromStr(privateKeyString);
        Assertions.assertNotNull(privateKey);
        log.info("获取Base64公钥(非压缩)");
        uncompressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(uncompressedPublicKeyString);
        Assertions.assertNotNull(uncompressedPublicKey);
        Assertions.assertNotNull(privateKey);
        log.info("获取Base64公钥(压缩)");
        compressedPublicKey = BcEcKeyUtils.getPublicKeyFromStr(compressedPublicKeyString);
        Assertions.assertNotNull(compressedPublicKey);

    }

    @RepeatedTest(10000)
    @Execution(ExecutionMode.CONCURRENT)
    void test01() throws InvalidCipherTextException {
        String plainText = "你好，World！Hello, 世界!" + UlidCreator.getUlid();
        log.info("加密: {}", plainText);

        String encryptedData = Sm2Utils.encrypt(plainText, uncompressedPublicKey);
        log.info("公钥(非压缩)加密后的数据: {}", encryptedData);
        String decryptText = Sm2Utils.decrypt(encryptedData, privateKey);
        log.info("解密后的文本: {}", decryptText);
        Assertions.assertEquals(plainText, decryptText);

        encryptedData = Sm2Utils.encrypt(plainText, compressedPublicKey);
        log.info("公钥(压缩)加密后的数据: {}", encryptedData);
        decryptText = Sm2Utils.decrypt(encryptedData, privateKey);
        log.info("解密后的文本: {}", decryptText);
        Assertions.assertEquals(plainText, decryptText);
    }

}
