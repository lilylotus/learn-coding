package cn.nihility.common.exception;

/**
 * @author nihility
 * @date 2022/02/16 14:17
 */
public class HttpRequestException extends RuntimeException {

    private static final long serialVersionUID = -2314943357090542057L;

    public HttpRequestException(String message) {
        super(message);
    }

    public HttpRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpRequestException(Throwable cause) {
        super(cause);
    }

}
