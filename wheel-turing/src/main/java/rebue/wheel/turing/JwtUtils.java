package rebue.wheel.turing;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;

import com.nimbusds.jwt.SignedJWT;

import lombok.extern.slf4j.Slf4j;
import rebue.wheel.core.LocalDateTimeUtils;

@Slf4j
public class JwtUtils {

    public final static String JWT_TOKEN_NAME = "jwt_token";

    /**
     * 将JWT签名添加到Cookie中
     *
     * @param sign           JWT的签名
     * @param expirationTime JWT签名的过期时间
     */
    public static void addCookie(final String sign, final LocalDateTime expirationTime, final ServerHttpResponse resp)
    {
        log.info("将JWT签名添加到Cookie中");
        final ResponseCookie responseCookie = ResponseCookie.from(JWT_TOKEN_NAME, sign).maxAge((int) ((LocalDateTimeUtils.getMillis(expirationTime) - System.currentTimeMillis()) / 1000)).path("/").build();
        resp.addCookie(responseCookie);
    }

    /**
     * 从请求的Cookie中获取JWT签名信息
     *
     * @return JWT的签名
     */
    public static String getSignFromCookies(final ServerHttpRequest req)
    {
        log.info("从请求的Cookie中获取JWT签名信息");
        final MultiValueMap<String, HttpCookie> cookies = req.getCookies();
        if (cookies != null && !cookies.isEmpty()) {
            final List<HttpCookie> cookieList = cookies.get(JWT_TOKEN_NAME);
            if (cookieList != null && !cookieList.isEmpty()) {
                return cookieList.get(0).getValue();
            }
        }
        return null;
    }

    /**
     * 从签名中获取JWT信息中的账户ID
     *
     * @return 如果没有此项，会抛出NumberFormatException异常
     */
    public static Long getJwtAccountIdFromSign(String sign)
    {
        try {
            SignedJWT jwt = SignedJWT.parse(sign);
            return Long.valueOf(jwt.getJWTClaimsSet().getSubject());
        } catch (final ParseException e) {
            return null;
        }
    }

    /**
     * 从签名中获取JWT信息中的附加信息
     *
     * @return 返回Map&lt;String, Object&gt;，再通过key可获得里面的项
     * 例如:
     * result.get("orgId")可获得当前用户的组织ID
     * result.get("isTester")可获得当前用户是否是测试者
     */
    public static Map<String, Object> getJwtAdditionFromSign(final String sign) throws ParseException
    {
        SignedJWT jwt = SignedJWT.parse(sign);
        return (Map<String, Object>) jwt.getJWTClaimsSet().getClaim("addition");
    }

    /**
     * 从签名中获取JWT信息中的附加信息中的项
     *
     * @return 返回通过key可获得里面的项
     * 例如:
     * "orgId"可获得当前用户的组织ID
     * "isTester"可获得当前用户是否是测试者
     */
    public static Object getJwtAdditionItemFromSign(final String sign, final String key) throws ParseException
    {
        final Map<String, Object> additions = getJwtAdditionFromSign(sign);
        return additions == null ? null : additions.get(key);
    }

}
