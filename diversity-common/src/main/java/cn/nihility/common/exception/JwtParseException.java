package cn.nihility.common.exception;

public class JwtParseException extends RuntimeException {

    private static final long serialVersionUID = -3197623728603960170L;

    public JwtParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtParseException(String message) {
        super(message);
    }

}
