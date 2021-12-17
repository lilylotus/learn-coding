package cn.nihility.cloud.resilience4j.exception;

public class CircuitBreakerExceptionB extends RuntimeException {

    private static final long serialVersionUID = 72329178931137755L;

    public CircuitBreakerExceptionB() {
    }

    public CircuitBreakerExceptionB(String message) {
        super(message);
    }

    public CircuitBreakerExceptionB(String message, Throwable cause) {
        super(message, cause);
    }

    public CircuitBreakerExceptionB(Throwable cause) {
        super(cause);
    }

    public CircuitBreakerExceptionB(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
