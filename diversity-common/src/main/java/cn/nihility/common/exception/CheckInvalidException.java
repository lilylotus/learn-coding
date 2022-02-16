package cn.nihility.common.exception;

/**
 * @author nihility
 * @date 2022/02/16 13:42
 */
public class CheckInvalidException extends RuntimeException {

    private static final long serialVersionUID = -2190467613300397568L;

    public CheckInvalidException(String message) {
        super(message);
    }

    public CheckInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheckInvalidException(Throwable cause) {
        super(cause);
    }

}
