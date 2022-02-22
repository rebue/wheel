package rebue.wheel.turing;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.util.Arrays;
import java.util.Base64;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.spec.SM2ParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.util.encoders.Hex;

public class Sm2Utils {
    private static X9ECParameters  x9ECParameters  = GMNamedCurves.getByName("sm2p256v1");

    private static ECParameterSpec ecParameterSpec = new ECParameterSpec(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN());

    private static final Charset   DEFAULT_CHARSET = StandardCharsets.UTF_8;

    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
    }

    public static KeyPair generateKeyPair() {
        try {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
            keyPairGenerator.initialize(ecParameterSpec, new SecureRandom());
            final KeyPair kp = keyPairGenerator.generateKeyPair();
            return kp;
        } catch (final Exception e) {
            throw new RuntimeException(e);
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

    public static BCECPublicKey getPublicKeyFromString(final String publicKeyStrBase64) throws Exception {
        final String          publicKeyString = new String(Base64.getDecoder().decode(publicKeyStrBase64.getBytes(DEFAULT_CHARSET)), DEFAULT_CHARSET);
        final byte[]          publicKey       = Hex.decode(publicKeyString);
        final int             SM2_KEY_LEN     = 32;
        final int             offset          = 1;
        final BigInteger      x               = new BigInteger(1, Arrays.copyOfRange(publicKey, 0 + offset, SM2_KEY_LEN + offset));
        final BigInteger      y               = new BigInteger(1, Arrays.copyOfRange(publicKey, SM2_KEY_LEN + offset, SM2_KEY_LEN * 2 + offset));
        final ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(x9ECParameters.getCurve().createPoint(x, y), ecParameterSpec);
        return new BCECPublicKey("EC", ecPublicKeySpec, BouncyCastleProvider.CONFIGURATION);
    }

    public static BCECPrivateKey getPrivateKeyFromString(final String privateKeyStrBase64) throws Exception {
        final String           privateKeyString = new String(Base64.getDecoder().decode(privateKeyStrBase64.getBytes(DEFAULT_CHARSET)), DEFAULT_CHARSET);
        final byte[]           privateKey       = Hex.decode(privateKeyString);
        final ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(new BigInteger(1, privateKey), ecParameterSpec);
        return new BCECPrivateKey("EC", ecPrivateKeySpec, BouncyCastleProvider.CONFIGURATION);
    }

    /**
     *
     * @param msg
     * @param userId
     * @param privateKey
     * 
     * @return r||s，直接拼接byte数组的rs
     */
    public static byte[] signSm3WithSm2(final byte[] msg, final byte[] userId, final PrivateKey privateKey) {
        return rsAsn1ToPlainByteArray(signSm3WithSm2Asn1Rs(msg, userId, privateKey));
    }

    /**
     *
     * @param msg
     * @param userId
     * @param privateKey
     * 
     * @return rs in <b>asn1 format</b>
     */
    public static byte[] signSm3WithSm2Asn1Rs(final byte[] msg, final byte[] userId, final PrivateKey privateKey) {
        try {
            final SM2ParameterSpec parameterSpec = new SM2ParameterSpec(userId);
            final Signature        signer        = Signature.getInstance("SM3withSM2", "BC");
            signer.setParameter(parameterSpec);

            signer.initSign(privateKey, new SecureRandom());
            signer.update(msg, 0, msg.length);
            final byte[] sig = signer.sign();
            return sig;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param msg
     * @param userId
     * @param rs        r||s，直接拼接byte数组的rs
     * @param publicKey
     * 
     * @return
     */
    public static boolean verifySm3WithSm2(final byte[] msg, final byte[] userId, final byte[] rs, final PublicKey publicKey) {
        return verifySm3WithSm2Asn1Rs(msg, userId, rsPlainByteArrayToAsn1(rs), publicKey);
    }

    /**
     *
     * @param msg
     * @param userId
     * @param rs        in <b>asn1 format</b>
     * @param publicKey
     * 
     * @return
     */
    public static boolean verifySm3WithSm2Asn1Rs(final byte[] msg, final byte[] userId, final byte[] rs, final PublicKey publicKey) {
        try {
            final SM2ParameterSpec parameterSpec = new SM2ParameterSpec(userId);
            final Signature        verifier      = Signature.getInstance("SM3withSM2", "BC");
            verifier.setParameter(parameterSpec);
            verifier.initVerify(publicKey);
            verifier.update(msg, 0, msg.length);
            return verifier.verify(rs);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final static int RS_LEN = 32;

    private static byte[] bigIntToFixexLengthBytes(final BigInteger rOrS) {
        // for sm2p256v1, n is 00fffffffeffffffffffffffffffffffff7203df6b21c6052b53bbf40939d54123,
        // r and s are the result of mod n, so they should be less than n and have length<=32
        final byte[] rs = rOrS.toByteArray();
        if (rs.length == RS_LEN) {
            return rs;
        }
        else if (rs.length == RS_LEN + 1 && rs[0] == 0) {
            return Arrays.copyOfRange(rs, 1, RS_LEN + 1);
        }
        else if (rs.length < RS_LEN) {
            final byte[] result = new byte[RS_LEN];
            Arrays.fill(result, (byte) 0);
            System.arraycopy(rs, 0, result, RS_LEN - rs.length, rs.length);
            return result;
        }
        else {
            throw new RuntimeException("err rs: " + Hex.toHexString(rs));
        }
    }

    /**
     * BC的SM3withSM2签名得到的结果的rs是asn1格式的，这个方法转化成直接拼接r||s
     * 
     * @param rsDer rs in asn1 format
     * 
     * @return sign result in plain byte array
     */
    private static byte[] rsAsn1ToPlainByteArray(final byte[] rsDer) {
        final ASN1Sequence seq    = ASN1Sequence.getInstance(rsDer);
        final byte[]       r      = bigIntToFixexLengthBytes(ASN1Integer.getInstance(seq.getObjectAt(0)).getValue());
        final byte[]       s      = bigIntToFixexLengthBytes(ASN1Integer.getInstance(seq.getObjectAt(1)).getValue());
        final byte[]       result = new byte[RS_LEN * 2];
        System.arraycopy(r, 0, result, 0, r.length);
        System.arraycopy(s, 0, result, RS_LEN, s.length);
        return result;
    }

    /**
     * BC的SM3withSM2验签需要的rs是asn1格式的，这个方法将直接拼接r||s的字节数组转化成asn1格式
     * 
     * @param sign in plain byte array
     * 
     * @return rs result in asn1 format
     */
    private static byte[] rsPlainByteArrayToAsn1(final byte[] sign) {
        if (sign.length != RS_LEN * 2) {
            throw new RuntimeException("err rs. ");
        }
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
}
