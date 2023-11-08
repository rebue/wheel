package rebue.wheel.turing;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.spec.AlgorithmParameterSpec;

/**
 * 签名工具
 */
public class SignUtils {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 签名-SHA256withRSA算法
     *
     * @param privateKey 签名的私钥
     * @param data       要签名的数据
     * @return 签名
     */
    public static byte[] signByRsa(PrivateKey privateKey, byte[] data) {
        return sign("SHA256withRSA", privateKey, data, null);
    }

    /**
     * 校验签名-SHA256withRSA算法
     *
     * @param publicKey 要校验签名的公钥
     * @param data      要校验签名的数据
     * @param sign      要校验是否正确的签名
     * @return 签名是否正确
     */
    public static boolean verifyByRsa(PublicKey publicKey, byte[] data, byte[] sign) {
        return verify("SHA256withRSA", publicKey, data, sign, null);
    }

    /**
     * 签名-SHA256withECDSA算法
     *
     * @param privateKey 签名的私钥
     * @param data       要签名的数据
     * @return 签名
     */
    public static byte[] signByEcdsa(PrivateKey privateKey, byte[] data) {
        return sign("SHA256withECDSA", privateKey, data, null);
    }

    /**
     * 校验签名-SHA256withECDSA算法
     *
     * @param publicKey 要校验签名的公钥
     * @param data      要校验签名的数据
     * @param sign      要校验是否正确的签名
     * @return 签名是否正确
     */
    public static boolean verifyByEcdsa(PublicKey publicKey, byte[] data, byte[] sign) {
        return verify("SHA256withECDSA", publicKey, data, sign, null);
    }

    /**
     * 签名-SM3withSM2算法
     *
     * @param privateKey 签名的私钥
     * @param data       要签名的数据
     * @param initParams 签名算法的初始化参数
     * @return 签名
     */
    public static byte[] signBySm2(PrivateKey privateKey, byte[] data, AlgorithmParameterSpec initParams) {
        return sign("SM3withSM2", privateKey, data, initParams);
    }

    /**
     * 校验签名-SM3withSM2算法
     *
     * @param publicKey  要校验签名的公钥
     * @param data       要校验签名的数据
     * @param sign       要校验是否正确的签名
     * @param initParams 校验签名算法的初始化参数
     * @return 签名是否正确
     */
    public static boolean verifyBySm2(PublicKey publicKey, byte[] data, byte[] sign, AlgorithmParameterSpec initParams) {
        return verify("SM3withSM2", publicKey, data, sign, initParams);
    }

    /**
     * 签名
     *
     * @param signAlgorithm 签名算法(所谓签名其实就是先进行摘要，再对摘要进行加密，所以签名算法的名称一般为摘要算法名称+"with"+加密算法名称)
     * @param privateKey    签名的私钥
     * @param data          要签名的数据
     * @param initParams    签名算法的初始化参数
     * @return 签名
     */
    public static byte[] sign(String signAlgorithm, PrivateKey privateKey, byte[] data, AlgorithmParameterSpec initParams) {
        try {
            Signature signature = Signature.getInstance(signAlgorithm, "BC");
            signature.initSign(privateKey);
            if (initParams != null) {
                signature.setParameter(initParams);
            }
            signature.update(data);
            return signature.sign();
        } catch (NoSuchProviderException e) {
            throw new RuntimeException("未加载BC库");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("错误的签名算法名称: " + signAlgorithm, e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("提供了错误的私钥");
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException("错误的初始化参数");
        } catch (SignatureException e) {
            throw new RuntimeException("签名出错");
        }
    }

    /**
     * 校验签名
     *
     * @param signAlgorithm 签名算法(所谓签名其实就是先进行摘要，再对摘要进行加密，所以签名算法的名称一般为摘要算法名称+"with"+加密算法名称)
     * @param publicKey     要校验签名的公钥
     * @param data          要校验签名的数据
     * @param sign          要校验是否正确的签名
     * @param initParams    校验签名算法的初始化参数
     * @return 签名是否正确
     */
    public static boolean verify(String signAlgorithm, PublicKey publicKey, byte[] data, byte[] sign, AlgorithmParameterSpec initParams) {
        try {
            Signature signature = Signature.getInstance(signAlgorithm, "BC");
            signature.initVerify(publicKey);
            if (initParams != null) {
                signature.setParameter(initParams);
            }
            signature.update(data);
            return signature.verify(sign);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException("未加载BC库");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("错误的签名算法名称: " + signAlgorithm, e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("提供了错误的公钥");
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException("错误的初始化参数");
        } catch (SignatureException e) {
            throw new RuntimeException("校验签名出错");
        }
    }

}
