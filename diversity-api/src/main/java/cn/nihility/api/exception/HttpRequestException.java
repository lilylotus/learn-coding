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
    private final HttpStatus httpStatus;

    /**
     * 返回的响应消息体，可填
     * 不为空是返回该 body 对象，为空时获取异常信息在组装响应体
     */
    private final UnifyBaseResult body;


    public HttpRequestException(String message) {
        super(message);
        body = null;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public HttpRequestException(String message, Throwable cause) {
        super(message, cause);
        body = null;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public HttpRequestException(String message, HttpStatus status) {
        super(message);
        body = null;
        this.httpStatus = status;
    }

    public HttpRequestException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        body = null;
        this.httpStatus = status;
    }

    public HttpRequestException(HttpStatus httpStatus, UnifyBaseResult body) {
        super(body.getMessage());
        this.httpStatus = httpStatus;
        this.body = body;
    }

    public HttpRequestException(Throwable cause, HttpStatus httpStatus, UnifyBaseResult body) {
        super(body.getMessage(), cause);
        this.httpStatus = httpStatus;
        this.body = body;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public UnifyBaseResult getBody() {
        return body;
    }

}
