package cn.nihility.common.pojo;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nihility
 * @date 2022/02/16 18:10
 */
public class ResponseHolder<T> {

    /**
     * 返回内容
     */
    private T content;
    /**
     * 返回状态码
     */
    private int statusCode;
    /**
     * http 请求响应的 header 参数
     */
    private Map<String, String> headers;
    /**
     * http 请求响应的 cookies 参数
     */
    private Map<String, String> cookies;
    /**
     * 请求异常内容
     */
    private String errorContent;

    public void addCookie(String key, String value) {
        if (cookies == null) {
            cookies = new HashMap<>(8);
        }
        if (StringUtils.isNotBlank(value)) {
            cookies.put(key, value);
        }
    }

    public boolean ok() {
        return HttpStatus.SC_OK == statusCode;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers == null ? Collections.emptyMap() : headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getErrorContent() {
        return errorContent;
    }

    public void setErrorContent(String errorContent) {
        this.errorContent = errorContent;
    }

    public Map<String, String> getCookies() {
        return cookies == null ? Collections.emptyMap() : cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    @Override
    public String toString() {
        return "ResponseHolder{" +
            "content=" + content +
            ", statusCode=" + statusCode +
            ", headers=" + headers +
            ", cookies=" + cookies +
            ", errorContent=" + errorContent +
            '}';
    }

}
