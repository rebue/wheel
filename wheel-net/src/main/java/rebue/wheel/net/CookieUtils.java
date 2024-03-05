package rebue.wheel.net;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class CookieUtils {
    public static String Strict = "Strict";
    public static String Lax    = "Lax";
    public static String None   = "None";

    public final static String getValue(final HttpServletRequest req, final String key) {
        final Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (key.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public final static String getValue(final ServerHttpRequest req, final String key) {
        final MultiValueMap<String, HttpCookie> cookies = req.getCookies();
        if (cookies != null && !cookies.isEmpty()) {
            final List<HttpCookie> cookieList = cookies.get(key);
            if (cookieList != null && !cookieList.isEmpty()) {
                return cookieList.get(0).getValue();
            }
        }
        return null;
    }

    public final static String getValue(final ServerHttpResponse req, final String key) {
        final MultiValueMap<String, ResponseCookie> cookies = req.getCookies();
        if (cookies != null && !cookies.isEmpty()) {
            final List<ResponseCookie> cookieList = cookies.get(key);
            if (cookieList != null && !cookieList.isEmpty()) {
                return cookieList.get(0).getValue();
            }
        }
        return null;
    }

    /**
     * 设置cookie path默认为/
     *
     * @param response    响应
     * @param name        属性名
     * @param valueString 属性值
     * @param maxAge      时间/s
     */
    public final static void setCookie(final ServerHttpResponse response, final String name,
                                       final String valueString, long maxAge) {
        setCookie(response, name, valueString, maxAge, null, false);
    }

    /**
     * 设置cookie
     *
     * @param response       响应
     * @param name           属性名
     * @param valueString    属性值
     * @param maxAge         时间/s
     * @param path           null 则为/
     * @param isSameSiteNone null则不设置
     */
    public final static void setCookie(final ServerHttpResponse response, final String name,
                                       final String valueString, long maxAge, final String path, final Boolean isSameSiteNone) {
        final ResponseCookieBuilder from = ResponseCookie.from(name, valueString);
        from.maxAge(maxAge);
        if (StringUtils.isBlank(path)) {
            from.path("/");
        } else {
            from.path(path);
        }
        from.secure(false);
        if (isSameSiteNone) {
            from.sameSite(None);
            from.secure(true);
        }
        final ResponseCookie responseCookie = from.build();
        response.addCookie(responseCookie);
    }

    public static class sameSite {
        static String Strict = "Strict";
        static String Lax    = "Lax";
        static String None   = "None";
    }
}
