package rebue.wheel.turing;

import com.github.f4b6a3.ulid.UlidCreator;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.nimbusds.jwt.SignedJWT;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.text.ParseException;
import java.util.Map;

public class JwtUtils {
    /**
     * 默认的曲线算法
     */
    private static final Curve        DEFAULT_CURVE          = Curve.P_521;
    /**
     * 默认的签名算法
     */
    private static final JWSAlgorithm DEFAULT_SIGN_ALGORITHM = JWSAlgorithm.ES512;

    /**
     * 生成JWK
     *
     * @return JWK
     * @throws JOSEException 生成JWK异常
     */
    public static JWK genJwk() throws JOSEException {
        return genJwk(DEFAULT_CURVE);
    }

    /**
     * 生成JWK
     *
     * @return JWK
     * @throws JOSEException 生成JWK异常
     */
    public static JWK genJwk(Curve curve) throws JOSEException {
        return new ECKeyGenerator(curve).generate();
    }

    /**
     * 将公钥转成JWK
     *
     * @param publicKey 公钥
     * @return JWK
     */
    public static JWK toJwk(PublicKey publicKey) {
        return toJwk(publicKey, DEFAULT_CURVE);
    }

    /**
     * 将公钥转成JWK
     *
     * @param publicKey 公钥
     * @param curve     曲线算法
     * @return JWK
     */
    public static JWK toJwk(PublicKey publicKey, Curve curve) {
        return new ECKey.Builder(curve, (ECPublicKey) publicKey).build();
    }

    /**
     * 将公私钥对转成JWK
     *
     * @param privateKey 私钥
     * @param publicKey  公钥
     * @return JWK
     */
    public static JWK toJwk(PrivateKey privateKey, PublicKey publicKey) {
        return toJwk(privateKey, publicKey, DEFAULT_CURVE);
    }

    /**
     * 将公私钥对转成JWK
     *
     * @param privateKey 私钥
     * @param publicKey  公钥
     * @param curve      曲线算法
     * @return JWK
     */
    public static JWK toJwk(PrivateKey privateKey, PublicKey publicKey, Curve curve) {
        return new ECKey.Builder(curve, (ECPublicKey) publicKey).privateKey(privateKey).build();
    }

    /**
     * 根据含有私钥的JWK生成签名器
     *
     * @param privateJwk 含有私钥的JWK
     * @return 签名器
     * @throws JOSEException 生成签名器异常
     */
    public static JWSSigner toJwsSigner(JWK privateJwk) throws JOSEException {
        return new ECDSASigner((ECKey) privateJwk);
    }

    /**
     * 根据含有公钥的JWK生成验签器
     *
     * @param publicJwk 含有公钥的JWK
     * @return 验签器
     * @throws JOSEException 生成验签器异常
     */
    public static JWSVerifier toJwsVerifier(JWK publicJwk) throws JOSEException {
        return new ECDSAVerifier((ECKey) publicJwk);
    }

    /**
     * 签名
     *
     * @param jwsSigner 签名器
     * @param json      要签名的JSON格式的数据
     * @return 签名字符串
     * @throws JOSEException 签名异常
     */
    public static String sign(JWSSigner jwsSigner, String json) throws JOSEException {
        return sign(jwsSigner, json, DEFAULT_SIGN_ALGORITHM);
    }

    /**
     * 签名
     *
     * @param jwsSigner     签名器
     * @param json          要签名的JSON格式的数据
     * @param signAlgorithm 签名算法
     * @return 签名字符串
     * @throws JOSEException 签名异常
     */
    public static String sign(JWSSigner jwsSigner, String json, JWSAlgorithm signAlgorithm) throws JOSEException {
        JsonObject jsonObject = (JsonObject) JsonParser.parseString(json);
        Long       current    = System.currentTimeMillis() / 1000;

        jsonObject.addProperty("nbf", current);                                 // Not Before(不能在此时间之前)
        jsonObject.addProperty("iat", current);                                 // Issued at(在什么时候签发的)
        jsonObject.addProperty("jti", UlidCreator.getUlid().toLowerCase());     // JWT ID
        // Creates the JWS object with payload
        JWSObject jwsObject = new JWSObject(new JWSHeader.Builder(signAlgorithm)
                .type(JOSEObjectType.JWT)
                .keyID(UlidCreator.getUlid().toLowerCase())
                .build(),
                new Payload(jsonObject.toString()));
        // Compute the EC signature
        jwsObject.sign(jwsSigner);
        // Serialize the JWS to compact form
        return jwsObject.serialize();
    }

    /**
     * 校验签名
     *
     * @param jwsVerifier 签验器
     * @param sign        签名
     * @return 签名是否正确
     * @throws ParseException 解析签名异常
     * @throws JOSEException  校验异常
     */
    public static boolean verify(JWSVerifier jwsVerifier, String sign) throws ParseException, JOSEException {
        // On the consumer side, parse the JWS and verify its EC signature
        SignedJWT signedJwt = SignedJWT.parse(sign);
        return signedJwt.verify(jwsVerifier);
    }

    /**
     * 获取签名中的信息
     *
     * @param sign 签名
     * @return 签名中的信息
     * @throws ParseException 解析签名异常
     */
    public static Map<String, Object> getClaims(String sign) throws ParseException {
        SignedJWT signedJwt = SignedJWT.parse(sign);
        return signedJwt.getJWTClaimsSet().getClaims();
    }
}
