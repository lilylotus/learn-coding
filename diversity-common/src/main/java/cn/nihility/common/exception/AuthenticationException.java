package cn.nihility.common.exception;

import cn.nihility.common.pojo.UnifyBaseResult;

/**
 * @author nihility
 * @date 2022/02/18 13:24
 */
public class AuthenticationException extends BusinessException {

    private static final long serialVersionUID = 4479554324416378134L;

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationException(String message, int statusCode) {
        super(message, statusCode);
    }

    public AuthenticationException(String message, Throwable cause, int statusCode) {
        super(message, cause, statusCode);
    }

    public AuthenticationException(UnifyBaseResult body, int statusCode) {
        super(body, statusCode);
    }

    public AuthenticationException(Throwable cause, UnifyBaseResult body, int statusCode) {
        super(cause, body, statusCode);
    }

}
