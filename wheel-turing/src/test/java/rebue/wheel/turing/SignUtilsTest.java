package rebue.wheel.turing;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jcajce.spec.SM2ParameterSpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class SignUtilsTest {
    // 固定值，SM2签名的标识
    private static final byte[]           USER_ID   = "1234567812345678".getBytes(StandardCharsets.UTF_8);
    private static final SM2ParameterSpec sm2Params = new SM2ParameterSpec(USER_ID);

    /**
     * 测试RSA签名与验签
     */
    @Test
    public void test01() {
        byte[]     data       = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ张三李四王五".getBytes(StandardCharsets.UTF_8);
        KeyPair    keyPair    = KeyUtils.generateKeyPair("RSA", 2048);
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey  publicKey  = keyPair.getPublic();
        byte[]     sign       = SignUtils.signByRsa(privateKey, data);
        boolean    verified   = SignUtils.verifyByRsa(publicKey, data, sign);
        Assertions.assertTrue(verified);
        sign[sign.length - 3] = 'a';
        sign[sign.length - 2] = 'b';
        sign[sign.length - 1] = 'c';
        verified = SignUtils.verifyByRsa(publicKey, data, sign);
        Assertions.assertFalse(verified);
    }

    /**
     * 测试ECDSA签名与验签
     */
    @Test
    public void test02() {
        byte[]     data       = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ张三李四王五".getBytes(StandardCharsets.UTF_8);
        KeyPair    keyPair    = BcEcKeyUtils.generateKeyPair(BcEcKeyUtils.EcAlgorithm.ECDSA);
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey  publicKey  = keyPair.getPublic();
        byte[]     sign       = SignUtils.signByEcdsa(privateKey, data);
        boolean    verified   = SignUtils.verifyByEcdsa(publicKey, data, sign);
        Assertions.assertTrue(verified);
        sign[sign.length - 3] = 'a';
        sign[sign.length - 2] = 'b';
        sign[sign.length - 1] = 'c';
        verified = SignUtils.verifyByEcdsa(publicKey, data, sign);
        Assertions.assertFalse(verified);
    }

    /**
     * 测试SM2签名与验签
     */
    @Test
    public void test03() {
        byte[]     data       = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ张三李四王五".getBytes(StandardCharsets.UTF_8);
        KeyPair    keyPair    = BcEcKeyUtils.generateKeyPair(BcEcKeyUtils.EcAlgorithm.SM2);
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey  publicKey  = keyPair.getPublic();
        byte[]     sign       = SignUtils.signBySm2(privateKey, data, sm2Params);
        boolean    verified   = SignUtils.verifyBySm2(publicKey, data, sign, sm2Params);
        Assertions.assertTrue(verified);
        sign[sign.length - 3] = 'a';
        sign[sign.length - 2] = 'b';
        sign[sign.length - 1] = 'c';
        verified = SignUtils.verifyBySm2(publicKey, data, sign, sm2Params);
        Assertions.assertFalse(verified);
    }
}
