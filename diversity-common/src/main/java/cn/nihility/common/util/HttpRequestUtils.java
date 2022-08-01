package cn.nihility.common.util;

import cn.nihility.common.constant.Constant;
import cn.nihility.common.pojo.ResponseHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author nihility
 */
public class HttpRequestUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestUtils.class);

    private static final String COOKIE_JSESSIONID = "JSESSIONID";
    private static final String SERVLET_FORWARD_REQUEST_URI = "javax.servlet.forward.request_uri";
    private static final String AMPERSAND = "&";
    private static final String EQUAL = "=";

    private HttpRequestUtils() {
    }

    public static String urlEncode(final String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        try {
            return URLEncoder.encode(content, Constant.UTF8);
        } catch (UnsupportedEncodingException e) {
            logger.error(" [{}] 进行 URL 编码异常", content, e);
            return content;
        }
    }

    public static String urlDecode(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        try {
            return URLDecoder.decode(content, Constant.UTF8);
        } catch (UnsupportedEncodingException e) {
            logger.error(" [{}] 进行 URL 解码异常", content, e);
            return content;
        }
    }

    public static String urlDecodePure(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        String t1 = urlDecode(content);
        String t2 = urlDecode(t1);
        while (!t1.equals(t2)) {
            t1 = t2;
            t2 = urlDecode(t1);
        }
        return t2;
    }

    public static String addUrlParam(final String url, String key, String value, boolean urlEncode) {
        if (StringUtils.isBlank(url)) {
            return url;
        }
        if (urlEncode) {
            return url.contains("?") ? url + "&" + key + "=" + urlEncode(value) : url + "?" + key + "=" + urlEncode(value);
        } else {
            return url.contains("?") ? url + "&" + key + "=" + value : url + "?" + key + "=" + value;
        }
    }

    public static String addUrlParam(final String url, String key, String value) {
        return addUrlParam(url, key, value, true);
    }

    public static String addUrlParams(String url, Map<String, String> params) {
        int index = url.indexOf("?");
        String encodeUrl = urlParamsEncode(url);
        StringJoiner paramJoiner = new StringJoiner("&");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (null != entry.getValue()) {
                paramJoiner.add(entry.getKey() + "=" + urlEncode(entry.getValue()));
            }
        }
        String encodeParam = paramJoiner.toString();
        return index > 0 ? encodeUrl + "&" + encodeParam : encodeUrl + "?" + encodeParam;
    }

    public static String addEncodeUrlParam(final String url, String key, String value) {
        return addUrlParam(url, key, value, true);
    }

    public static String splitEncodeUrlParam(String urlParams) {

        final String pureParams = urlDecodePure(urlParams);

        StringJoiner joiner = new StringJoiner("&");
        String[] param = pureParams.split(AMPERSAND);
        for (String item : param) {
            String[] sp = item.split(EQUAL);
            joiner.add(sp[0] + EQUAL + urlEncode(sp[1]));
        }

        return joiner.toString();
    }

    public static String urlParamsEncode(final String url) {
        if (StringUtils.isBlank(url)) {
            return url;
        }

        int index = url.indexOf("?");

        return index > 0 ?
            url.substring(0, index) + "?" + splitEncodeUrlParam(url.substring(index + 1)) :
            url;
    }

    public static String obtainHeaderValue(String key, HttpServletRequest request) {
        return (StringUtils.isBlank(key) || request == null) ? null : request.getHeader(key);
    }

    public static String obtainParamValue(String key, HttpServletRequest request) {
        return (StringUtils.isBlank(key) || request == null) ? null : request.getParameter(key);
    }

    public static String obtainCookieValue(String key, HttpServletRequest request) {
        if (StringUtils.isBlank(key) || request == null) {
            return null;
        }
        String value = null;
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    value = cookie.getValue();
                    break;
                }
            }
        }
        return value;
    }

    public static String obtainCookieJsessionid(HttpServletRequest request) {
        return obtainCookieValue(COOKIE_JSESSIONID, request);
    }

    public static String obtainRequestValue(String key, HttpServletRequest request) {

        if (StringUtils.isBlank(key) || request == null) {
            return null;
        }

        String value = request.getParameter(key);
        if (StringUtils.isBlank(value)) {
            value = request.getHeader(key);
            if (StringUtils.isBlank(value)) {
                Cookie[] cookies = request.getCookies();
                if (null != cookies) {
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals(key)) {
                            value = cookie.getValue();
                            break;
                        }
                    }
                }
            }
        }

        return value;
    }

    /**
     * 获取原请求URI地址
     */
    public static String getOriginatingRequestUri(HttpServletRequest request) {
        return Optional.ofNullable(Objects.toString(request.getAttribute(SERVLET_FORWARD_REQUEST_URI), null))
            .orElse(request.getRequestURI());
    }

    /**
     * 获取原始的请求 URI 地址
     */
    public static String getOriginRequestUrl(HttpServletRequest request) {
        String host = request.getHeader("Host");
        String scheme = request.getHeader("X-Forwarded-Proto");
        if (StringUtils.isNotBlank(scheme)) {
            int index = scheme.indexOf(",");
            if (index > 0) {
                scheme = scheme.substring(0, index);
            }
        }
        if (StringUtils.isBlank(scheme)) {
            scheme = "http";
        }
        if (StringUtils.isBlank(host)) {
            host = request.getServerName();
        }
        return scheme + "://" + host;
    }

    public static String bearerTokenValue(String bearerToken) {
        if (null != bearerToken && bearerToken.startsWith(Constant.AUTHENTICATION_BEARER_TOKEN_PREFIX)) {
            bearerToken = bearerToken.substring(Constant.AUTHENTICATION_BEARER_TOKEN_PREFIX.length());
        }
        return bearerToken;
    }

    public static Map<String, String> cookiesToMap(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        final Map<String, String> result = new HashMap<>();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                result.put(cookie.getName(), cookie.getValue());
            }
        }
        return result;
    }

    public static Map<String, String> paramsToMap(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        Map<String, String> result = new HashMap<>(8);
        if (null != params && !params.isEmpty()) {
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                result.put(entry.getKey(), entry.getValue()[0]);
            }
        }
        return result;
    }

    public static void addCookie(String name, String value,
                                 String domain, String path, int expiry,
                                 HttpServletResponse response) {
        if (StringUtils.isBlank(name) || response == null) {
            return;
        }

        Cookie cookie = new Cookie(name, value);
        cookie.setPath(StringUtils.isBlank(path) ? "/" : path);
        cookie.setMaxAge(expiry);
        // 禁止 JS 读取
        cookie.setHttpOnly(true);
        // 在 HTTP 下也传输
        cookie.setSecure(false);
        if (StringUtils.isNotBlank(domain)) {
            cookie.setDomain(domain);
        }

        response.addCookie(cookie);
    }

    public static void addCookie(String name, String value, HttpServletResponse response) {
        addCookie(name, value, null, null, -1, response);
    }

    public static void addCookieJsessionid(HttpServletResponse response) {
        addCookie(COOKIE_JSESSIONID, UuidUtils.jdkUUID(), response);
    }

    public static void addCookieJsessionidIfAbsent(HttpServletRequest request, HttpServletResponse response) {
        String id = obtainCookieJsessionid(request);
        if (null == id) {
            addCookieJsessionid(response);
        }
    }

    public static void addCookie(String name, String value, int expiry,
                                 HttpServletResponse response) {
        addCookie(name, value, null, null, expiry, response);
    }

    public static URI buildUri(final String url, Map<String, String> params) {
        try {
            URIBuilder builder = new URIBuilder(url);
            if (params != null && !params.isEmpty()) {
                params.forEach(builder::addParameter);
            }
            return builder.build();
        } catch (URISyntaxException e) {
            logger.error("构建 URL [{}] 异常", url, e);
            final StringJoiner joiner = new StringJoiner("&");
            String buildUrl = url;
            if (params != null && !params.isEmpty()) {
                params.forEach((k, v) -> joiner.add(k + "=" + urlEncode(v)));
                if (url.lastIndexOf("?") > 0) {
                    buildUrl = url + "&" + joiner.toString();
                } else {
                    buildUrl = url + "?" + joiner.toString();
                }
            }
            return URI.create(buildUrl);
        }
    }

    public static URI buildUri(final String url) {
        return buildUri(url, null);
    }

    public static void setApplicationJsonHeader(final HttpUriRequest request) {
        if (null != request) {
            request.setHeader(HTTP.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        }
    }

    public static void setFormHeader(final HttpUriRequest request) {
        if (null != request) {
            request.setHeader(HTTP.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
        }
    }

    public static void addHeaders(final HttpUriRequest request, Map<String, String> headers) {
        if (request != null && headers != null && !headers.isEmpty()) {
            headers.forEach(request::addHeader);
        }
    }

    public static void addHeader(final HttpUriRequest request, String name, String value) {
        if (request != null && StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value)) {
            request.addHeader(name, value);
        }
    }

    public static String parseRedirectUrl(ResponseHolder<?> response) {
        return (null == response || response.getStatusCode() != Constant.RESPONSE_REDIRECT_STATUS_VALUE) ? null :
            response.getHeaders().get(Constant.RESPONSE_HEADER_REDIRECT);
    }

    public static Map<String, String> headersToMap(Header[] allHeaders) {
        Map<String, String> headers = new HashMap<>(8);
        if (null != allHeaders) {
            Stream.of(allHeaders).forEach(header -> headers.put(header.getName(), header.getValue()));
        }
        return headers;
    }

    public static Map<String, String> requestHeadersToMap(HttpServletRequest request) {
        if (null == request) {
            return Collections.emptyMap();
        }
        Map<String, String> headers = new HashMap<>(8);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            headers.put(key, value);
        }
        return headers;
    }

    public static StringEntity buildJsonStringEntity(String content) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        StringEntity entity = new StringEntity(content, ContentType.APPLICATION_JSON);
        entity.setContentEncoding(Constant.UTF8);
        return entity;
    }

    public static UrlEncodedFormEntity buildUrlEncodedFormEntity(Map<String, String> params) {
        if (null == params || params.isEmpty()) {
            return null;
        }
        final List<NameValuePair> pairList = new ArrayList<>();
        params.forEach((k, v) -> pairList.add(new BasicNameValuePair(k, v)));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairList, StandardCharsets.UTF_8);
        entity.setContentEncoding(Constant.UTF8);
        return entity;
    }

    public static void setEntity(final HttpEntityEnclosingRequestBase request, final HttpEntity entity) {
        if (null != request && null != entity) {
            request.setEntity(entity);
        }
    }

}
