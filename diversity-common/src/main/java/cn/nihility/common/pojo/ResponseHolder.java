package cn.nihility.common.pojo;

import java.util.Collections;
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

    @Override
    public String toString() {
        return "ResponseHolder{" +
            "content=" + content +
            '}';
    }
}