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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class RequestUtil {

    private static final Logger logger = LoggerFactory.getLogger(RequestUtil.class);

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

        return preUrl + "?" + urlEncode(encodeParams);
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

    public static void addFromHeader(final HttpUriRequest request) {
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
