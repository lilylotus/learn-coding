package cn.nihility.unify.idempotent;

import cn.nihility.unify.exception.IdempotentException;

/**
 * 校验幂等性 token 接口
 */
public interface VerifyIdempotentToken {

    boolean verify(String token) throws IdempotentException;

}
