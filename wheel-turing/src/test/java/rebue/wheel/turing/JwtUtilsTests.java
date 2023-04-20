package rebue.wheel.turing;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Base64;

@Slf4j
public class JwtUtilsTests {
    @Test
    public void test() throws JOSEException, ParseException, InvalidKeySpecException {
        JWK jwk = JwtUtils.genJwk();
        log.info("jwk: {}", jwk);
        log.info("jwk.computeThumbprint: {}", jwk.computeThumbprint());
        log.info("jwk.toJSONObject: {}", jwk.toJSONObject());
        log.info("jwk.toPublicJWK: {}", jwk.toPublicJWK());
        log.info("jwk.toJSONString: {}", jwk.toJSONString());
        log.info("jwk.getX509CertChain: {}", jwk.getX509CertChain());
        log.info("jwk.getX509CertURL: {}", jwk.getX509CertURL());
        log.info("jwk.getKeyStore: {}", jwk.getKeyStore());
        String ecPrivateKeyEncode = KeyUtils.encode(((ECKey) jwk).toECPrivateKey());
        log.info("jwk.toECPrivateKey: {}", ecPrivateKeyEncode);
        String ecPublicKeyEncode = KeyUtils.encode(((ECKey) jwk).toECPublicKey());
        log.info("jwk.toECPublicKey: {}", ecPublicKeyEncode);
        String privateKeyEncode = KeyUtils.encode(((ECKey) jwk).toPrivateKey());
        log.info("jwk.toECPrivateKey: {}", privateKeyEncode);
        String publicKeyEncode = KeyUtils.encode(((ECKey) jwk).toPublicKey());
        log.info("jwk.toECPublicKey: {}", publicKeyEncode);
        PrivateKey privateKey = EcKeyUtils.getPrivateKeyFromString(privateKeyEncode);
        log.info("jwk.privateKey: {}", new String(Base64.getEncoder().encode(privateKey.getEncoded())));
        PublicKey publicKey = EcKeyUtils.getPublicKeyFromString(publicKeyEncode);
        log.info("jwk.publicKey: {}", new String(Base64.getEncoder().encode(publicKey.getEncoded())));
        JWK privateKeyJwk = JwtUtils.toJwk(privateKey, publicKey);
        log.info("privateKey.jwk.toJSONString: {}", privateKeyJwk.toJSONString());
        JWK publicKeyJwk = JwtUtils.toJwk(publicKey);
        log.info("publicKey.jwk.toJSONString: {}", publicKeyJwk.toJSONString());
        JWSSigner jwsSigner = JwtUtils.toJwsSigner(privateKeyJwk);
        String    sign      = JwtUtils.sign(jwsSigner, "{\"sub\":\"1000\",\"name\":\"管理员\",\"chgAgenCode\":\"10000\",\"admDivCode\":\"450000\"}");
        log.info("sign: {}", sign);
        String[] signSplit = sign.split("\\.");
        String   header    = new String(Base64.getUrlDecoder().decode(signSplit[0]));
        String   payload   = new String(Base64.getUrlDecoder().decode(signSplit[1]));
        byte[]   signature = Base64.getUrlDecoder().decode(signSplit[2]);
        log.info("decode:\nheader-{}\npayload-{}\nsignature-{}", header, payload, Hex.toHexString(signature));
        JWSVerifier jwsVerifier  = JwtUtils.toJwsVerifier(publicKeyJwk);
        boolean     verifyResult = JwtUtils.verify(jwsVerifier, sign);
        log.info("verify: {}", verifyResult);
        Assertions.assertTrue(verifyResult);
        log.info("claims: {}", JwtUtils.getClaims(sign));
        log.info("修改签名");
        signature[7] = 'a';
        sign = Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes())
                + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes())
                + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        log.info("sign: {}", sign);
        verifyResult = JwtUtils.verify(jwsVerifier, sign);
        log.info("verify: {}", verifyResult);
        Assertions.assertFalse(verifyResult);
    }

}
