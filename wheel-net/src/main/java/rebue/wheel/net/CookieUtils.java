package rebue.wheel.net;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

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
}
