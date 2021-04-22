package rebue.wheel.turing;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.extern.slf4j.Slf4j;
import rebue.wheel.core.LocalDateUtils;

@Slf4j
public class JwtUtils {
    private final static String JWT_TOKEN_NAME = "jwt_token";

    /**
     * JWT签名
     *
     * @param key
     *                  签名的密钥
     * @param claimsSet
     *                  JWT中payload部分的内容
     *
     * @throws JOSEException
     *                       签名失败
     */
    public static String sign(final byte[] key, final JWTClaimsSet claimsSet) throws JOSEException {
        log.info("开始计算JWT签名");

        if (key.length < 64) {
            throw new IllegalArgumentException("密钥的字节长度不能小于64个Byte，目前是" + key.length + "个Byte");
        }

        // Prepare JWT with claims set
        final SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS512), claimsSet);

        // Create HMAC signer
        final JWSSigner signer = new MACSigner(key);

        // Apply the HMAC protection
        signedJWT.sign(signer);

        // Serialize to compact form, produces something like
        // eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
        final String sign = signedJWT.serialize();
        final String msg  = "JWT签名成功";
        log.info("{}: {}", msg, sign);
        return sign;
    }

    /**
     * 将JWT签名添加到Cookie中
     *
     * @param sign
     *                       JWT的签名
     * @param expirationTime
     *                       JWT签名的过期时间
     */
    public static void addCookie(final String sign, final Date expirationTime, final HttpServletResponse resp) {
        addCookie(sign, expirationTime.getTime(), resp);
    }

    /**
     * 将JWT签名添加到Cookie中
     *
     * @param sign
     *                       JWT的签名
     * @param expirationTime
     *                       JWT签名的过期时间
     */
    public static void addCookie(final String sign, final LocalDateTime expirationTime, final HttpServletResponse resp) {
        addCookie(sign, LocalDateUtils.getMillis(expirationTime), resp);
    }

    /**
     * 将JWT签名添加到Cookie中
     *
     * @param sign
     *                       JWT的签名
     * @param expirationTime
     *                       JWT签名的过期时间(1970年1月1日零时至此的毫秒数)
     */
    public static void addCookie(final String sign, final Long expirationTime, final HttpServletResponse resp) {
        log.info("将JWT签名添加到Cookie中");
        final Cookie cookie = new Cookie(JWT_TOKEN_NAME, sign);
        cookie.setMaxAge((int) ((expirationTime - System.currentTimeMillis()) / 1000));
        cookie.setPath("/");
        resp.addCookie(cookie);
    }

    /**
     * 从请求的Cookie中获取JWT签名信息
     *
     * @return JWT的签名
     */
    public static String getSignInCookies(final HttpServletRequest req) {
        log.info("从请求的Cookie中获取JWT签名信息");
        final Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
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
     *                     要验证的签名
     *
     * @return 返回SignedJWT的实体(可从中取出head和payload部分进行验证，再校验签名是否正确)
     *
     * @throws ParseException
     *                        解析失败
     */
    public static SignedJWT parse(final String toVerifySign) throws ParseException {
        log.info("解析JWT签名: {}", toVerifySign);
        if (StringUtils.isBlank(toVerifySign)) {
            throw new IllegalArgumentException("JWT签名不能为空");
        }
        return SignedJWT.parse(toVerifySign);
    }

    /**
     * 校验签名是否正确
     *
     * @param key
     *                  签名的密钥
     * @param signedJWT
     *                  签名的实体
     *
     * @return 校验是否正确
     *
     * @throws JOSEException
     *                       校验失败
     */
    public static boolean verify(final byte[] key, final SignedJWT signedJWT) throws JOSEException {
        log.info("校验签名是否正确");
        final JWSVerifier verifier = new MACVerifier(key);
        final boolean     result   = signedJWT.verify(verifier);
        if (result) {
            log.info("JWT的签名正确");
        }
        else {
            log.info("JWT的签名不正确");
        }
        return result;
    }

    /**
     * 从请求的Cookie中获取JWT项的集合
     */
    public static JWTClaimsSet getJwtItemsInCookie(final HttpServletRequest req) throws ParseException {
        // 从请求的Cookie中获取JWT签名信息
        final String sign = JwtUtils.getSignInCookies(req);
        // 解析签名
        final SignedJWT signedJWT = JwtUtils.parse(sign);
        // 从签名中获取JWT项的集合
        return signedJWT.getJWTClaimsSet();
    }

    /**
     * 从请求的Cookie中获取JWT的指定项
     */
    public static Object getJwtItemInCookie(final HttpServletRequest req, final String key) throws ParseException {
        return getJwtItemsInCookie(req).getClaim(key);
    }

    /**
     * 从请求的Cookie中获取JWT信息中的用户ID
     *
     * @return 如果没有此项，会抛出NumberFormatException异常
     */
    public static Long getJwtAccountIdInCookie(final HttpServletRequest req) {
        try {
            return Long.valueOf((String) getJwtItemInCookie(req, "accountId"));
        } catch (final ParseException e) {
            return null;
        }
    }

    /**
     * 从请求的Cookie中获取JWT信息中的系统ID
     */
    public static String getJwtSysIdInCookie(final HttpServletRequest req) {
        try {
            return (String) getJwtItemInCookie(req, "sysId");
        } catch (final ParseException e) {
            return null;
        }
    }

    /**
     * 从请求的Cookie中获取JWT信息中的附加信息
     *
     * @return 返回Map&lt;String, Object&gt;，再通过key可获得里面的项
     *         例如:
     *         result.get("orgId")可获得当前用户的组织ID
     *         result.get("isTester")可获得当前用户是否是测试者
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getJwtAdditionInCookie(final HttpServletRequest req) throws ParseException {
        return (Map<String, Object>) getJwtItemInCookie(req, "addition");
    }

    /**
     * 从请求的Cookie中获取JWT信息中的附加信息中的项
     *
     * @return 返回通过key可获得里面的项
     *         例如:
     *         "orgId"可获得当前用户的组织ID
     *         "isTester"可获得当前用户是否是测试者
     */
    public static Object getJwtAdditionItemInCookie(final HttpServletRequest req, final String key) throws ParseException {
        final Map<String, Object> additions = getJwtAdditionInCookie(req);
        return additions == null ? null : additions.get(key);
    }

}
