package cn.nihility.common.exception;

import cn.nihility.common.pojo.UnifyBaseResult;
import org.apache.http.HttpStatus;

/**
 * HTTP 请求处理业务异常，会修改指定的 http header 状态码
 * @author nihility
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1142060456665910382L;

    /**
     * Http 响应的状态码
     */
    private int statusCode = HttpStatus.SC_BAD_REQUEST;

    /**
     * 返回的响应消息体，可填
     * 不为空是返回该 body 对象，为空时获取异常信息在组装响应体
     */
    private UnifyBaseResult body;


    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String message, int statusCode) {
        super(message);
        body = null;
        this.statusCode = statusCode;
    }

    public BusinessException(String message, Throwable cause,  int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public BusinessException(UnifyBaseResult body, int statusCode) {
        super(body.getMessage());
        this.statusCode = statusCode;
        this.body = body;
    }

    public BusinessException(Throwable cause, UnifyBaseResult body, int statusCode) {
        super(body.getMessage(), cause);
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public UnifyBaseResult getBody() {
        return body;
    }

    public void setBody(UnifyBaseResult body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "BusinessException{" +
            "statusCode=" + statusCode +
            ", body=" + body +
            '}';
    }

}
