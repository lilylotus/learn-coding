package cn.nihility.unify.exception;

import cn.nihility.unify.pojo.UnifyResultCode;

public class UnifyException extends RuntimeException {

    private static final long serialVersionUID = 342912393687438606L;

    private UnifyResultCode resultCode;

    public UnifyException(UnifyResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public UnifyException(String message, UnifyResultCode resultCode) {
        super(message);
        this.resultCode = resultCode;
    }

    public UnifyException(String message, Throwable cause, UnifyResultCode resultCode) {
        super(message, cause);
        this.resultCode = resultCode;
    }

    public UnifyResultCode getResultCode() {
        return resultCode;
    }
}
