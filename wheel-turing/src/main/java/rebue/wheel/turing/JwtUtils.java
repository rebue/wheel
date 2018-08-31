package rebue.wheel.turing;

import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class JwtUtils {
    private final static Logger _log           = LoggerFactory.getLogger(JwtUtils.class);

    private final static String JWT_TOKEN_NAME = "jwt_token";

    /**
     * JWT签名
     * 
     * @param key
     *            签名的密钥
     * @param claimsSet
     *            JWT中payload部分的内容
     * @throws JOSEException
     *             签名失败
     */
    public static String sign(byte[] key, JWTClaimsSet claimsSet) throws JOSEException {
        _log.info("开始计算JWT签名");

        // Prepare JWT with claims set
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS512), claimsSet);

        // Create HMAC signer
        JWSSigner signer = new MACSigner(key);

        // Apply the HMAC protection
        signedJWT.sign(signer);

        // Serialize to compact form, produces something like
        // eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
        String sign = signedJWT.serialize();
        String msg = "JWT签名成功";
        _log.info("{}: {}", msg, sign);
        return sign;
    }

    /**
     * 将JWT签名添加到Cookie中
     * 
     * @param sign
     *            JWT的签名
     * @param expirationTime
     *            JWT签名的过期时间
     */
    public static void addCookie(String sign, Date expirationTime, HttpServletResponse resp) {
        _log.info("将JWT签名添加到Cookie中");
        Cookie cookie = new Cookie(JWT_TOKEN_NAME, sign);
        cookie.setMaxAge((int) ((expirationTime.getTime() - System.currentTimeMillis()) / 1000));
        cookie.setPath("/");
        resp.addCookie(cookie);
    }

    /**
     * 从请求的Cookie中获取JWT签名信息
     * 
     * @return JWT的签名
     */
    public static String getSignInCookies(HttpServletRequest req) {
        _log.info("从请求的Cookie中获取JWT签名信息");
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (JWT_TOKEN_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 解析要验证的签名(因为签名的几部分是用base64编码的，先解析出来)
     * 
     * @param toVerifySign
     *            要验证的签名
     * @return 返回SignedJWT的实体(可从中取出head和payload部分进行验证，再校验签名是否正确)
     * @throws ParseException
     *             解析失败
     */
    public static SignedJWT parse(final String toVerifySign) throws ParseException {
        _log.info("解析JWT签名: {}", toVerifySign);
        return SignedJWT.parse(toVerifySign);
    }

    /**
     * 校验签名是否正确
     * 
     * @param key
     *            签名的密钥
     * @param signedJWT
     *            签名的实体
     * @return 校验是否正确
     * @throws JOSEException
     *             校验失败
     */
    public static boolean verify(byte[] key, SignedJWT signedJWT) throws JOSEException {
        _log.info("校验签名是否正确");
        JWSVerifier verifier = new MACVerifier(key);
        boolean result = signedJWT.verify(verifier);
        if (result)
            _log.info("JWT的签名正确");
        else
            _log.info("JWT的签名不正确");
        return result;
    }

    /**
     * 从请求的Cookie中获取JWT项的集合
     */
    public static JWTClaimsSet getJwtItemsInCookie(HttpServletRequest req) throws ParseException {
        // 从请求的Cookie中获取JWT签名信息
        String sign = JwtUtils.getSignInCookies(req);
        // 解析签名
        SignedJWT signedJWT = JwtUtils.parse(sign);
        // 从签名中获取JWT项的集合
        return signedJWT.getJWTClaimsSet();
    }

    /**
     * 从请求的Cookie中获取JWT的指定项
     */
    public static Object getJwtItemInCookie(HttpServletRequest req, String key) throws ParseException {
        return getJwtItemsInCookie(req).getClaim(key);
    }

    /**
     * 从请求的Cookie中获取JWT信息中的用户ID
     */
    public static Long getJwtUserIdInCookie(HttpServletRequest req) throws NumberFormatException, ParseException {
        return Long.valueOf((String) getJwtItemInCookie(req, "userId"));
    }

}
