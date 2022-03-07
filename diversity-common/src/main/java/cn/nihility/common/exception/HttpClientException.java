package cn.nihility.common.exception;

/**
 * @author nihility
 * @date 2022/02/16 14:17
 */
public class HttpClientException extends RuntimeException {

    private static final long serialVersionUID = -2314943357090542057L;

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpClientException(Throwable cause) {
        super(cause);
    }

}
