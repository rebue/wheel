package rebue.wheel.net;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;

public class CookieUtils {
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

}
