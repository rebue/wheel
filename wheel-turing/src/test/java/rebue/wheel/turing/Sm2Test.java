package rebue.wheel.turing;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class Sm2Test {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    // 固定值，SM2签名的标识
    private static final String USERID = "UwMDAwMjAwMDAwMDAwMDAxODciLAog";

    // 生成公钥Base64编码字符串，该公钥字符串提供给其他系统
    // 广西统一公共收付系统提供的测试环境公钥是（生产环境另外提供）：
    private static final String privateKey = "MTliNDAyMzBmNDM3ZWEzOTg4NjM1YmUyNmIxMGFiNTdiY2Q2YzQ0YzYzOTYwYjAyNzIwMTc3Yzk2YTUxNWE5Zg==";
    private static final String publicKey  = "MDQ1NGFjNWYyZTc0YmZlOWQzMjZkMTJiY2RiZDg5ODdhNzM2MzBmNTg2OGJjNjQxNGY2OTQzNjE0YWUwNDE1N2UzYjAyOWMwNWUwMDY5YTUxMWQ3YzVhZTdhZmExN2I4NmM0ZmQ4ODc4YzE2Y2MyNWRkMjRjZDY0NDA2MDk3MjQ4Yg==";
    private static final String plainText  = "authCode=auth0000001&requestTime=1482809327714&bizParam=eyJwYXlDb2RlIjoiMTgxMTA0MTU0NzExMTEwMSJ9";

    @Test
    public void test01() throws Exception {
        // 生成签名
        final BCECPrivateKey bcecPrivateKey = Sm2Utils.getPrivateKeyFromString(privateKey);
        final String         sign           = sign(bcecPrivateKey, plainText);
        System.out.println("生成签名： " + sign);
    }

    @Test
    public void test02() throws Exception {
        // 验证签名
        final BCECPublicKey bcecPublicKey = Sm2Utils.getPublicKeyFromString(publicKey);
        System.out.println("验签结果：" + verifySign(bcecPublicKey, plainText,
                "MzA0NDAyMjA3YzIwZTQ2Nzg4ZTQzMWUzYjc4ZjM5N2MyMGYzZTIxMDcyZTZlYWY4NDM4NGQwNWM5OTQwOThlOGJkMDI4YTQxMDIyMDUxMzQ5MGQ3YjlmNzMwYzJjOWQ0NmQ4NDUxMDMwNzA3ZTU4MmZlY2UzNDk5MmZlYmRiNTQ3NjQyOWJlZjYxMzA="));
    }

    @Test
    public void test03() throws Exception {
        // 生成公私钥对
        final KeyPair keyPair = Sm2Utils.generateKeyPair();

        // 生成私钥Base64编码字符串，各自系统保存好自己的私钥，不外泄
        final String privateKey = Sm2Utils.getPrivateKeyString(keyPair);
        System.out.println("私钥:" + privateKey);

        // 生成公钥Base64编码字符串，该公钥字符串提供给其他系统
        // 广西统一公共收付系统提供的测试环境公钥是（生产环境另外提供）：MDRhNzg4ZmU1NWRmZjYxN2Y3NTI2M2EyMjVjZWU5NzljODdkYzVkYjQ4ZDllOTljNTc3MTdmZWY0YzZlMmY1ZGMzNmQ0MTRjMzJjMjRlZjE4NTM0MGUwZTg2YjlkYjA1NzBhNzIxNzRiZTQ0OTgyNmQ5MmM3NDEwYzFkMzFiMGYxZQ==
        final String publicKey = Sm2Utils.getPublicKeyString(keyPair);
        System.out.println("公钥:" + publicKey);

        // 签名参数（按该顺序排列）,其中bizParam参数是业务参数的Base64编码
        final String plainText = "authCode=auth0000001&requestTime=1482809327714&bizParam=eyJwYXlDb2RlIjoiMTgxMTA0MTU0NzExMTEwMSJ9";

        // 生成签名
        final BCECPrivateKey bcecPrivateKey = Sm2Utils.getPrivateKeyFromString(privateKey);
        final String         sign           = sign(bcecPrivateKey, plainText);
        System.out.println("生成签名： " + sign);

        // 验证签名
        final BCECPublicKey bcecPublicKey = Sm2Utils.getPublicKeyFromString(publicKey);
        System.out.println("验签结果：" + verifySign(bcecPublicKey, plainText, sign));
    }

    /**
     * 私钥签名
     * 
     * @param privateKey 私钥
     * @param plainText  签名参数
     */
    public static String sign(final PrivateKey privateKey, final String plainText) throws UnsupportedEncodingException {
        System.out.println("USERID: " + USERID);
        System.out.println("msg: " + plainText);
        final byte[] userId = USERID.getBytes(DEFAULT_CHARSET);
        final byte[] msg    = plainText.getBytes(DEFAULT_CHARSET);
        final byte[] signed = Sm2Utils.signSm3WithSm2(msg, userId, privateKey);
        return encodeBase64Str(signed);
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
            final byte[] signed = decodeBase64(sign.getBytes(DEFAULT_CHARSET));
            final byte[] msg    = plainText.getBytes(DEFAULT_CHARSET);
            final byte[] userId = USERID.getBytes(DEFAULT_CHARSET);
            return Sm2Utils.verifySm3WithSm2(msg, userId, signed, publicKey);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Base64编码
     */
    public static String encodeBase64Str(final byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes), DEFAULT_CHARSET);
    }

    /**
     * Base64解码
     */
    public static byte[] decodeBase64(final byte[] bytes) {
        return Base64.getDecoder().decode(bytes);
    }
}
