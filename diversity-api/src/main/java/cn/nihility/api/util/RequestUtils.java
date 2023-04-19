package cn.nihility.api.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

/**
 * @author nihility
 * @date 2023-04-19 14:13
 */
public class RequestUtils {

    private RequestUtils() {
    }

    /**
     * Cookie 构建
     *
     * @param response http 响应
     * @param name     cookie name
     * @param value    cookie value
     * @param maxAge   cookie 过期时间
     * @param domain   域名
     * @param path     路径
     * @param secure   安全模式，false
     * @param httpOnly 仅 http 请求
     * @param sameSite 相同站点 Lax/Strict
     */
    public static void buildCookie(HttpServletResponse response, String name, String value,
                                   Duration maxAge, String domain, String path,
                                   boolean secure, boolean httpOnly, String sameSite) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value);
        if (null != maxAge) {
            builder.maxAge(maxAge);
        }
        if (null != domain) {
            builder.domain(domain);
        }
        if (null != path) {
            builder.path(path);
        }
        if (null != sameSite) {
            builder.sameSite(sameSite);
        }
        builder.secure(secure);
        builder.httpOnly(httpOnly);
        String cookie = builder.build().toString();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie);
    }

    public static void deleteCookie(String name, HttpServletResponse response) {
        buildCookie(response, name, "", Duration.ofSeconds(0L), null, null, false, true, null);
    }

    public static void buildCookie(HttpServletResponse response, String name, String value) {
        buildCookie(response, name, value, null, null, null, false, true, null);
    }

    public static void buildCookie(HttpServletResponse response, String name, String value, Duration maxAge) {
        buildCookie(response, name, value, maxAge, null, null, false, true, null);
    }

}
