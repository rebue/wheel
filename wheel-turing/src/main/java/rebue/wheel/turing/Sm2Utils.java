package rebue.wheel.turing;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.spec.SM2ParameterSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
public class Sm2Utils {
    private static final SM2Engine sm2Engine = new SM2Engine(new SM3Digest(), SM2Engine.Mode.C1C3C2);

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * 加密
     *
     * @param plainText 要加密的明文文本
     * @param publicKey 公钥
     * @return 加密后的数据(经过了Base64编码得到的字符串)
     */
    public static String encrypt(String plainText, PublicKey publicKey) throws InvalidCipherTextException {
        return encrypt(plainText, publicKey, EncodeMode.BASE64);
    }

    /**
     * 加密
     *
     * @param plainText 要加密的明文文本
     * @param publicKey 公钥
     * @return 加密后的数据(经过了Base64编码得到的字符串)
     */
    public static String encrypt(String plainText, PublicKey publicKey, EncodeMode encodeMode) throws InvalidCipherTextException {
        byte[] data = encrypt(plainText.getBytes(DEFAULT_CHARSET), publicKey);
        switch (encodeMode) {
            case HEX:
                return Hex.toHexString(data);
            case BASE64:
                return Base64.getEncoder().encodeToString(data);
            case HEX_BASE64:
                return Base64.getEncoder().encodeToString(Hex.encode(data));
        }
        throw new RuntimeException("unsupported encode mode");
    }

    /**
     * 加密
     *
     * @param data      要加密的数据
     * @param publicKey 公钥
     * @return 加密后的数据
     */
    public static byte[] encrypt(byte[] data, PublicKey publicKey) throws InvalidCipherTextException {
        BCECPublicKey         ecPublicKey           = (BCECPublicKey) publicKey;
        ECParameterSpec       ecParameterSpec       = ecPublicKey.getParameters();
        ECDomainParameters    ecDomainParameters    = newEcDomainParameters(ecParameterSpec);
        ECPublicKeyParameters ecPublicKeyParameters = new ECPublicKeyParameters(ecPublicKey.getQ(), ecDomainParameters);
        sm2Engine.init(true, new ParametersWithRandom(ecPublicKeyParameters));
        return sm2Engine.processBlock(data, 0, data.length);
    }

    /**
     * 解密
     *
     * @param data       要解密的字符串数据(自动适配Hex或Base64编码，及先Hex再Base64编码的字符串)
     * @param privateKey 私钥
     * @return 解密后的数据
     */
    public static String decrypt(String data, PrivateKey privateKey) throws InvalidCipherTextException {
        return new String(decrypt(AutoDecoder.decode(data), privateKey), DEFAULT_CHARSET);
    }

    /**
     * 解密
     *
     * @param data       要解密的数据
     * @param privateKey 私钥
     * @return 解密后的数据
     */
    public static byte[] decrypt(byte[] data, PrivateKey privateKey) throws InvalidCipherTextException {
        BCECPrivateKey         sm2PrivateKey          = (BCECPrivateKey) privateKey;
        ECParameterSpec        ecParameterSpec        = sm2PrivateKey.getParameters();
        ECDomainParameters     ecDomainParameters     = newEcDomainParameters(ecParameterSpec);
        ECPrivateKeyParameters ecPrivateKeyParameters = new ECPrivateKeyParameters(sm2PrivateKey.getD(), ecDomainParameters);
        sm2Engine.init(false, ecPrivateKeyParameters);
        return sm2Engine.processBlock(data, 0, data.length);
    }

    /**
     * 签名
     *
     * @param msg        需要签名的内容
     * @param userId     签名需要用到的用户ID
     * @param privateKey 签名需要用到私钥
     * @return r||s，直接拼接byte数组的rs
     */
    public static byte[] signSm3WithSm2(final byte[] msg, final byte[] userId, final PrivateKey privateKey) {
        return rsAsn1ToPlainByteArray(signSm3WithSm2Asn1Rs(msg, userId, privateKey));
    }

    /**
     * 签名
     *
     * @param msg        需要签名的内容
     * @param userId     签名需要用到的用户ID
     * @param privateKey 签名需要用到私钥
     * @return rs in <b>asn1 format</b>
     */
    public static byte[] signSm3WithSm2Asn1Rs(final byte[] msg, final byte[] userId, final PrivateKey privateKey) {
        try {
            final SM2ParameterSpec parameterSpec = new SM2ParameterSpec(userId);
            final Signature        signer        = Signature.getInstance("SM3withSM2");
            signer.setParameter(parameterSpec);
            signer.initSign(privateKey);
            signer.update(msg, 0, msg.length);
            return signer.sign();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 校验签名
     *
     * @param msg       明文的内容
     * @param userId    签名需要用到的userId
     * @param sign      要验证的签名
     * @param publicKey 公钥
     * @return 签名是否正确
     */
    public static boolean verifySm3WithSm2(final byte[] msg, final byte[] userId, final byte[] sign, final PublicKey publicKey) {
        return verifySm3WithSm2Asn1Rs(msg, userId, rsPlainByteArrayToAsn1(sign), publicKey);
    }

    /**
     * 校验签名
     *
     * @param msg       明文的内容
     * @param userId    签名需要用到的userId
     * @param rs        in <b>asn1 format</b>
     * @param publicKey 公钥
     * @return 签名是否正确
     */
    public static boolean verifySm3WithSm2Asn1Rs(final byte[] msg, final byte[] userId, final byte[] rs, final PublicKey publicKey) {
        try {
            final SM2ParameterSpec parameterSpec = new SM2ParameterSpec(userId);
            final Signature        verifier      = Signature.getInstance("SM3withSM2");
            verifier.setParameter(parameterSpec);
            verifier.initVerify(publicKey);
            verifier.update(msg, 0, msg.length);
            return verifier.verify(rs);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final static int RS_LEN = 32;

    private static byte[] bigIntToFixedLengthBytes(final BigInteger rOrS) {
        // for sm2p256v1, n is 00fffffffeffffffffffffffffffffffff7203df6b21c6052b53bbf40939d54123,
        // r and s are the result of mod n, so they should be less than n and have length<=32
        final byte[] rs = rOrS.toByteArray();
        if (rs.length == RS_LEN) {
            return rs;
        }
        if (rs.length == RS_LEN + 1 && rs[0] == 0) {
            return Arrays.copyOfRange(rs, 1, RS_LEN + 1);
        }
        if (rs.length < RS_LEN) {
            final byte[] result = new byte[RS_LEN];
            Arrays.fill(result, (byte) 0);
            System.arraycopy(rs, 0, result, RS_LEN - rs.length, rs.length);
            return result;
        }
        throw new RuntimeException("err rs: " + Hex.toHexString(rs));
    }

    /**
     * BC的SM3withSM2签名得到的结果的rs是asn1格式的，这个方法转化成直接拼接r||s
     *
     * @param rsDer rs in asn1 format
     * @return sign result in plain byte array
     */
    private static byte[] rsAsn1ToPlainByteArray(final byte[] rsDer) {
        final ASN1Sequence seq    = ASN1Sequence.getInstance(rsDer);
        // r，s可能因为大正数的补0规则在第一个有效字节前面插了一个(byte)0，变成33个字节，在这里要修正回32个字节去
        final byte[]       r      = bigIntToFixedLengthBytes(ASN1Integer.getInstance(seq.getObjectAt(0)).getValue());
        final byte[]       s      = bigIntToFixedLengthBytes(ASN1Integer.getInstance(seq.getObjectAt(1)).getValue());
        final byte[]       result = new byte[RS_LEN * 2];
        System.arraycopy(r, 0, result, 0, r.length);
        System.arraycopy(s, 0, result, RS_LEN, s.length);
        return result;
    }

    /**
     * BC的SM3withSM2验签需要的rs是asn1格式的，这个方法将直接拼接r||s的字节数组转化成asn1格式
     *
     * @param sign in plain byte array
     * @return rs result in asn1 format
     */
    private static byte[] rsPlainByteArrayToAsn1(final byte[] sign) {
        if (sign.length != RS_LEN * 2) {
            throw new RuntimeException("err sign. ");
        }
        // c1x,c1y的第一个bit可能为1，这个时候要确保他们表示的大数一定是正数，所以new BigInteger符号强制设为正
        final BigInteger          r = new BigInteger(1, Arrays.copyOfRange(sign, 0, RS_LEN));
        final BigInteger          s = new BigInteger(1, Arrays.copyOfRange(sign, RS_LEN, RS_LEN * 2));
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(r));
        v.add(new ASN1Integer(s));
        try {
            return new DERSequence(v).getEncoded("DER");
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ECDomainParameters newEcDomainParameters(ECParameterSpec ecParameterSpec) {
        return new ECDomainParameters(ecParameterSpec.getCurve(), ecParameterSpec.getG(), ecParameterSpec.getN(), ecParameterSpec.getH(), ecParameterSpec.getSeed());
    }

}
