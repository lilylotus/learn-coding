package cn.nihility.api.exception;

/**
 * 幂等操作异常类
 */
public class IdempotentException extends RuntimeException {
    private static final long serialVersionUID = -4731355152210869526L;

    public IdempotentException() {
    }

    public IdempotentException(String message) {
        super(message);
    }

    public IdempotentException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdempotentException(Throwable cause) {
        super(cause);
    }

    public IdempotentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
