package cn.nihility.unify.exception;

import cn.nihility.unify.pojo.UnifyResultCode;

/**
 * 幂等性异常类
 */
public class IdempotentException extends UnifyException {

    private static final long serialVersionUID = -4804372581832072346L;

    public IdempotentException(UnifyResultCode resultCode) {
        super(resultCode);
    }

    public IdempotentException(String tipMessage, UnifyResultCode resultCode) {
        super(tipMessage, resultCode);
    }

    public IdempotentException(String tipMessage, Throwable cause, UnifyResultCode resultCode) {
        super(tipMessage, cause, resultCode);
    }
}
