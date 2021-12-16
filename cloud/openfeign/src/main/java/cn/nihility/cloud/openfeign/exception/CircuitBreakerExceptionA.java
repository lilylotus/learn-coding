package cn.nihility.cloud.openfeign.exception;

public class CircuitBreakerExceptionA extends RuntimeException {

    private static final long serialVersionUID = 72329178931137755L;

    public CircuitBreakerExceptionA() {
    }

    public CircuitBreakerExceptionA(String message) {
        super(message);
    }

    public CircuitBreakerExceptionA(String message, Throwable cause) {
        super(message, cause);
    }

    public CircuitBreakerExceptionA(Throwable cause) {
        super(cause);
    }

    public CircuitBreakerExceptionA(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
