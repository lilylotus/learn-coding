package cn.nihility.boot.exception;

/**
 * @author dandelion
 * @date 2020:06:27 11:05
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String msg) {
        super(msg);
    }

    public AuthenticationException() {
    }
}
