package cn.nihility.api.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import javax.servlet.http.HttpServletResponse;


/**
 * @author nihility
 * @date 2022/02/18 15:50
 */
public class CookieUtils {

    private CookieUtils() {
    }

    public static void setCookie(String key, String value, String domain, String path, long maxAgeSeconds,
                                boolean httpOnly, boolean setSameSite, HttpServletResponse response) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie
            // key & value
            .from(key, value)
            // 禁止js读取
            .httpOnly(httpOnly)
            // path
            .path(StringUtils.isNotBlank(path) ? path : "/")
            // 失效时间
            .maxAge(maxAgeSeconds);
        if (setSameSite) {
            builder
                // 设置sameSite为None时，该参数必须为true
                .secure(true)
                // Chrome 51 开始，浏览器的 Cookie 新增加了一个 SameSite 属性，用来防止 CSRF 攻击和用户追踪。
                .sameSite("None");
        } else {
            // 在http下也传输
            builder.secure(false);
        }
        if (StringUtils.isNotBlank(domain)) {
            // 域名
            builder.domain(domain);
        }
        // 设置Cookie Header
        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }

    public static void setCookie(String key, String value, String domain, String path,
                                 long maxAgeSeconds, HttpServletResponse response) {
        setCookie(key, value, domain, path, maxAgeSeconds, true, false, response);
    }

    public static void setCookie(String key, String value, long ttl, HttpServletResponse response) {
        setCookie(key, value, null, null, ttl, true, false, response);
    }

    public static void setCookie(String key, String value, HttpServletResponse response) {
        setCookie(key, value, null, null, -1, true, false, response);
    }

    public static void setCookie(String key, HttpServletResponse response) {
        setCookie(key, "", null, null, 0, true, false, response);
    }

}
