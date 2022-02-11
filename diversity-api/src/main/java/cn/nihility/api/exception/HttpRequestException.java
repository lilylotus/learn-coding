package cn.nihility.api.exception;

import cn.nihility.common.pojo.UnifyBaseResult;
import org.springframework.http.HttpStatus;

/**
 * HTTP 请求处理异常，会修改指定的 http header 状态码
 */
public class HttpRequestException extends RuntimeException {

    private static final long serialVersionUID = 1142060456665910382L;

    /**
     * 会修改响应 header 状态码为指定的状态码，必填
     */
    private final HttpStatus status;

    /**
     * 返回的响应消息体，可填
     * 不为空是返回该 body 对象，为空时获取异常信息在组装响应体
     */
    private final UnifyBaseResult body;

    public HttpRequestException(HttpStatus status, UnifyBaseResult body) {
        this.status = status;
        this.body = body;
    }

    public HttpRequestException(String message, HttpStatus status, UnifyBaseResult body) {
        super(message);
        this.status = status;
        this.body = body;
    }

    public HttpRequestException(String message, Throwable cause, HttpStatus status, UnifyBaseResult body) {
        super(message, cause);
        this.status = status;
        this.body = body;
    }

    public HttpRequestException(Throwable cause, HttpStatus status, UnifyBaseResult body) {
        super(cause);
        this.status = status;
        this.body = body;
    }

    public HttpRequestException(String message, Throwable cause, boolean enableSuppression,
                                boolean writableStackTrace, HttpStatus status, UnifyBaseResult body) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.status = status;
        this.body = body;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public UnifyBaseResult getBody() {
        return body;
    }

}
