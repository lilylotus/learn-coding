package cn.nihility.api.exception;

import cn.nihility.common.exception.BusinessException;

/**
 * 幂等操作异常类
 * @author nihility
 */
public class IdempotentException extends BusinessException {
    private static final long serialVersionUID = -4731355152210869526L;

    public IdempotentException(String message) {
        super(message);
    }

}
