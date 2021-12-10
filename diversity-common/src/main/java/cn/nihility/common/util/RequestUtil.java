package cn.nihility.common.util;

import cn.nihility.common.constant.Constant;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
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

public class RequestUtil {

    private static final Logger logger = LoggerFactory.getLogger(RequestUtil.class);

    private static final String COOKIE_JSESSIONID = "JSESSIONID";
    private static final String URL_SPLIT_AMPERSAND = "&";
    private static final String URL_PARAM_EQUAL = "=";

    private RequestUtil() {
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
        String t2 = urlDecode(content);
        while (!t1.equals(t2)) {
            t2 = t1;
            t1 = urlDecode(content);
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
        return addUrlParam(url, key, value, false);
    }

    public static String addEncodeUrlParam(final String url, String key, String value) {
        return addUrlParam(url, key, value, true);
    }

    public static String splitEncodeUrlParam(String params) {

        final String pureParams = urlDecodePure(params);

        StringJoiner joiner = new StringJoiner("&");
        String[] param = pureParams.split("&");
        for (String item : param) {
            String[] sp = item.split("=");
            joiner.add(sp[0] + "=" + urlEncode(sp[1]));
        }

        return joiner.toString();
    }

    public static String urlParamsEncode(final String url) {
        if (StringUtils.isBlank(url)) {
            return url;
        }

        int index = url.indexOf("?");
        String preUrl = url;
        String encodeParams = "";
        if (index > 0) {
            preUrl = url.substring(0, index);
            encodeParams = url.substring(index + 1);
        }

        return preUrl + "?" + splitEncodeUrlParam(encodeParams);
    }

    public static String obtainHttpRequestHeaderValue(String key, HttpServletRequest request) {
        return (StringUtils.isBlank(key) || request == null) ? null : request.getHeader(key);
    }

    public static String obtainHttpRequestParamValue(String key, HttpServletRequest request) {
        return (StringUtils.isBlank(key) || request == null) ? null : request.getParameter(key);
    }

    public static String obtainHttpRequestCookieValue(String key, HttpServletRequest request) {
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

    public static String obtainCookieJSESSIONID(HttpServletRequest request) {
        return obtainHttpRequestCookieValue(COOKIE_JSESSIONID, request);
    }

    public static String obtainHttpRequestValue(String key, HttpServletRequest request) {

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

    public static void addCookieJSESSIONID(HttpServletResponse response) {
        addCookie(COOKIE_JSESSIONID, UuidUtil.jdkUUID(), response);
    }

    public static void addCookieJSESSIONIDIfAbsent(HttpServletRequest request, HttpServletResponse response) {
        String id = obtainCookieJSESSIONID(request);
        if (null == id) {
            addCookieJSESSIONID(response);
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

    public static void addApplicationJsonHeader(final HttpUriRequest request) {
        if (null != request) {
            request.addHeader(HTTP.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        }
    }

    public static void addFormHeader(final HttpUriRequest request) {
        if (null != request) {
            request.addHeader(HTTP.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
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

    public static StringEntity buildJsonStringEntity(String content) {
        if (StringUtils.isBlank(content)) {
            throw new IllegalArgumentException("构建 application/json Entity 内容不可为空");
        }
        final StringEntity entity = new StringEntity(content,
            ContentType.create(ContentType.APPLICATION_JSON.getMimeType(), Consts.UTF_8));
        entity.setContentEncoding(Constant.UTF8);
        return entity;
    }

    public static UrlEncodedFormEntity buildUrlEncodedFormEntity(Map<String, String> params) {
        if (null == params || params.isEmpty()) {
            throw new IllegalArgumentException("构建 form 表单的参数不可为空");
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

    public static HttpPost buildJsonHttpPost(final URI uri, final String jsonBody) {
        HttpPost post = new HttpPost(uri);
        addApplicationJsonHeader(post);
        setEntity(post, buildJsonStringEntity(jsonBody));
        return post;
    }

}
